package de.uka.ilkd.key.logic.label;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.key_project.util.collection.ImmutableArray;

import de.uka.ilkd.key.java.JavaInfo;
import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.java.TypeConverter;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.PosInOccurrence;
import de.uka.ilkd.key.logic.PosInTerm;
import de.uka.ilkd.key.logic.Sequent;
import de.uka.ilkd.key.logic.SequentChangeInfo;
import de.uka.ilkd.key.logic.SequentFormula;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TermBuilder;
import de.uka.ilkd.key.logic.TermFactory;
import de.uka.ilkd.key.logic.op.Function;
import de.uka.ilkd.key.logic.op.Operator;
import de.uka.ilkd.key.logic.op.ProgramVariable;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.pp.PosInSequent;
import de.uka.ilkd.key.proof.Node;
import de.uka.ilkd.key.proof.mgt.SpecificationRepository;
import de.uka.ilkd.key.rule.label.OriginTermLabelRefactoring;

/**
 * <p> An {@link OriginTermLabel} saves a term's origin in the JML specification
 * ({@link #getOrigin()}) as well as the origins of all of its subterms and former
 * subterms ({@link #getSubtermOrigins()}). </p>
 *
 * <p> For this to work correctly, you must call
 * {@link #collectSubtermOrigins(Term, TermBuilder)} for every top-level formula in your
 * original proof obligation. </p>
 *
 * <p> Before doing this, you can call {@link TermBuilder#addLabelToAllSubs(Term, TermLabel)}
 * for every term you have added to the original contract in your PO to add an
 * {@link OriginTermLabel}
 * of your choosing. Terms for which you do not do this get a label of the form
 * {@code new OriginTermLabel(SpecType.NONE, null, -1)}. </p>
 *
 * @author lanzinger
 */
public class OriginTermLabel implements TermLabel {

    /**
     * Display name for {@link OriginTermLabel}s.
     */
    public final static Name NAME = new Name("origin");

    /**
     * @see #getChildCount()
     */
    public final static int CHILD_COUNT = 2;


    /**
     * Find a term's origin.
     * If the term has no origin, iterate through its parent terms until we find one with an origin.
     *
     * @param pis the position of the term whose origin to find.
     * @return the term's origin, or the origin of one of its parents.
     */
    public static Origin getOrigin(PosInSequent pis) {
        if (pis == null) {
            return null;
        }

        return getOrigin(pis.getPosInOccurrence());
    }

    /**
     * Find a term's origin.
     * If the term has no origin, iterate through its parent terms until we find one with an origin.
     *
     * @param pio the position of the term whose origin to find.
     * @return the term's origin, or the origin of one of its parents.
     */
    public static Origin getOrigin(PosInOccurrence pio) {
        if (pio == null) {
            return null;
        }

        Term term = pio.subTerm();

        OriginTermLabel originLabel =
                (OriginTermLabel) term.getLabel(OriginTermLabel.NAME);

        // If the term has no origin label,
        // iterate over its parent terms until we find one with an origin label,
        // then show that term's origin.
        while (originLabel == null && !pio.isTopLevel()) {
            pio = pio.up();
            term = pio.subTerm();

            originLabel =
                    (OriginTermLabel) term.getLabel(OriginTermLabel.NAME);
        }

        if (originLabel != null && originLabel.getOrigin().specType != SpecType.NONE) {
            return originLabel.getOrigin();
        } else {
            return null;
        }
    }

    /**
     * The term's origin.
     * @see #getOrigin()
     */
    private Origin origin;

    /**
     * The origins of the term's sub-terms and former sub-terms.
     * @see #getSubtermOrigins()
     */
    private Set<Origin> subtermOrigins;

    /**
     * Creates a new {@link OriginTermLabel}.
     *
     * @param origin the term's origin.
     */
    public OriginTermLabel(Origin origin) {
        this.origin = origin;
        this.subtermOrigins = new HashSet<>();
    }

    /**
     * Creates a new {@link OriginTermLabel}.
     *
     * @param origin the term's origin.
     * @param subtermOrigins the origins of the term's (former) subterms.
     */
    public OriginTermLabel(Origin origin, Set<Origin> subtermOrigins) {
        this(origin);
        this.subtermOrigins.addAll(subtermOrigins);
        this.subtermOrigins = this.subtermOrigins.stream()
                .filter(o -> o.specType != SpecType.NONE).collect(Collectors.toSet());
    }

    /**
     * Creates a new {@link OriginTermLabel}.
     *
     * @param subtermOrigins the origins of the term's (former) subterms.
     */
    public OriginTermLabel(Set<Origin> subtermOrigins) {
        this.origin = new Origin(SpecType.NONE);
        this.subtermOrigins = new HashSet<>();
        this.subtermOrigins.addAll(subtermOrigins);
        this.subtermOrigins = this.subtermOrigins.stream()
                .filter(o -> o.specType != SpecType.NONE).collect(Collectors.toSet());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((origin == null) ? 0 : origin.hashCode());
        result = prime * result + ((subtermOrigins == null) ? 0 : subtermOrigins.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OriginTermLabel) {
            OriginTermLabel other = (OriginTermLabel) obj;
            return other.origin.equals(origin) && other.subtermOrigins.equals(subtermOrigins);
        } else {
            return false;
        }
    }

    /**
     * <p> Determines whether an {@code OriginTermLabel} can be added to the specified term. </p>
     *
     * <p> E.g., no labels should be added to terms whose operator is a heap variable as this leads
     * to various problems during proof search. </p>
     *
     * @param term a term
     * @param services services.
     * @return {@code true} iff an {@code OriginTermLabel} can be added to the specified term.
     */
    public static boolean canAddLabel(Term term, Services services) {
        return canAddLabel(term.op(), services);
    }

    /**
     * <p> Determines whether an {@code OriginTermLabel} can be added to a term with the specified
     * operator. </p>
     *
     * <p> E.g., no labels should be added to terms whose operator is a heap variable as this leads
     * to various problems during proof search. </p>
     *
     * @param op the specified operator.
     * @param services services.
     * @return {@code true} iff an {@code OriginTermLabel} can be added to a term
     *  with the specified operator.
     */
    public static boolean canAddLabel(Operator op, Services services) {
        //TODO: Instead of not adding origin labels to certain terms, we should investigate why
        // adding labels to these kinds of terms breaks the prover and fix these issues.

        final TypeConverter tc = services.getTypeConverter();
        final JavaInfo ji = services.getJavaInfo();

        if (op.arity() == 0) {
            Sort sort = op.sort(new ImmutableArray<>());

            if (sort.extendsTrans(Sort.FORMULA)) {
                return true;
            } else if (op instanceof ProgramVariable) {
                return !sort.extendsTrans(tc.getHeapLDT().targetSort())
                        && !sort.extendsTrans(tc.getLocSetLDT().targetSort())
                        && !op.name().equals(ji.getInv().name())
                        && !op.name().toString().endsWith(SpecificationRepository.LIMIT_SUFFIX);
            } else {
                return false;
            }
        } else {
            return !(op instanceof Function)
                    || (op.getClass().equals(Function.class)
                            && ((Function) op).sort().extendsTrans(Sort.FORMULA));
        }
    }

    /**
     * Removes all {@link OriginTermLabel} from the specified sequent.
     *
     * @param seq the sequent to transform.
     * @param services services.
     * @return the resulting sequent change info.
     */
    public static SequentChangeInfo removeOriginLabels(Sequent seq, Services services) {
        SequentChangeInfo changes = null;

        for (int i = 1; i <= seq.size(); ++i) {
            SequentFormula oldFormula = seq.getFormulabyNr(i);
            SequentFormula newFormula = new SequentFormula(
                    OriginTermLabel.removeOriginLabels(oldFormula.formula(), services));
            SequentChangeInfo change = seq.changeFormula(
                    newFormula,
                    PosInOccurrence.findInSequent(seq, i, PosInTerm.getTopLevel()));

            if (changes == null) {
                changes = change;
            } else {
                changes.combine(change);
            }
        }

        return changes;
    }

    /**
     * Removes all {@link OriginTermLabel} from the specified term and its sub-terms.
     *
     * @param term the term to transform.
     * @param services services.
     * @return the transformed term.
     */
    public static Term removeOriginLabels(Term term, Services services) {
        if (term == null) {
            return null;
        }

        List<TermLabel> labels = term.getLabels().toList();
        final TermLabel originTermLabel = term.getLabel(NAME);
        final TermFactory tf = services.getTermFactory();
        final ImmutableArray<Term> oldSubs = term.subs();
        Term[] newSubs = new Term[oldSubs.size()];

        if (originTermLabel != null) {
            labels.remove(originTermLabel);
        }

        for (int i = 0; i < newSubs.length; ++i) {
            newSubs[i] = removeOriginLabels(oldSubs.get(i), services);
        }

        return tf.createTerm(term.op(),
                             newSubs,
                             term.boundVars(),
                             term.javaBlock(),
                             new ImmutableArray<>(labels));
    }

    /**
     * Compute the common origin from all origins in the passed origins set.
     * @param origins the passed origins set
     * @return the computed common origin
     */
    public static Origin computeCommonFileOrigin(final Set<FileOrigin> origins) {
        if (origins.isEmpty()) {
            return new Origin(SpecType.NONE);
        }

        SpecType commonSpecType = null;
        String commonFileName = null;
        int commonLine = -1;

        for (FileOrigin origin : origins) {
            if (commonSpecType == null) {
                commonSpecType = origin.specType;
            } else if (commonSpecType != origin.specType) {
                return new Origin(SpecType.NONE);
            }

            if (commonFileName == null) {
                commonFileName = origin.fileName;
            } else if (!commonFileName.equals(origin.fileName)) {
                return new Origin(SpecType.NONE);
            }

            if (commonLine == -1) {
                commonLine = origin.line;
            } else if (commonLine != origin.line) {
                return new Origin(SpecType.NONE);
            }
        }

        return new FileOrigin(commonSpecType, commonFileName, commonLine);
    }

    /**
     * Compute the common origin from all origins in the passed origins set.
     * @param origins the passed origins set
     * @return the computed common origin
     */
    public static Origin computeCommonNodeOrigin(final Set<NodeOrigin> origins) {
        if (origins.isEmpty()) {
            return new Origin(SpecType.NONE);
        }

        SpecType commonSpecType = SpecType.NONE;
        String commonRuleName = null;
        int commonNr = -1;

        for (NodeOrigin origin : origins) {
            if (commonSpecType == null) {
                commonSpecType = origin.specType;
            } else if (commonSpecType != origin.specType) {
                return new Origin(SpecType.NONE);
            }

            if (commonRuleName == null) {
                commonRuleName = origin.ruleName;
            } else if (!commonRuleName.equals(origin.ruleName)) {
                return new Origin(SpecType.NONE);
            }

            if (commonNr == -1) {
                commonNr = origin.nodeNr;
            } else if (commonNr != origin.nodeNr) {
                return new Origin(SpecType.NONE);
            }
        }

        return new NodeOrigin(commonSpecType, commonRuleName, commonNr);
    }

    /**
     * Compute the common origin from all origins in the passed origins set.
     * @param origins the passed origins set
     * @return the computed common origin
     */
    @SuppressWarnings("unchecked")
    public static Origin computeCommonOrigin(final Set<? extends Origin> origins) {
        if (origins.isEmpty()) {
            return new Origin(SpecType.NONE);
        }

        Iterator<? extends Origin> it = origins.iterator();
        Class<? extends Origin> clazz = it.next().getClass();

        while (it.hasNext()) {
            if (!it.next().getClass().equals(clazz)) {
                return new Origin(SpecType.NONE);
            }
        }

        if (clazz.equals(FileOrigin.class)) {
            return computeCommonFileOrigin((Set<FileOrigin>) origins);
        } else if (clazz.equals(NodeOrigin.class)) {
            return computeCommonNodeOrigin((Set<NodeOrigin>) origins);
        } else {
            SpecType commonSpecType = SpecType.NONE;

            for (Origin origin : origins) {
                if (commonSpecType == SpecType.NONE) {
                    commonSpecType = origin.specType;
                } else if (commonSpecType != origin.specType) {
                    return new Origin(SpecType.NONE);
                }
            }

            return new Origin(commonSpecType);
        }
    }

    /**
     * This method transforms a term in such a way that
     * every {@link OriginTermLabel} contains all of the correct
     * {@link #getSubtermOrigins()}.
     *
     * @param term the term to transform.
     * @param services services.
     * @return the transformed term.
     */
    public static Term collectSubtermOrigins(Term term, Services services) {
        if (!canAddLabel(term, services)) {
            return term;
        }

        SubTermOriginData newSubs = getSubTermOriginData(term.subs(), services);
        final ImmutableArray<TermLabel> labels =
                computeOriginLabelsFromSubTermOrigins(term, newSubs.origins);

        return services.getTermFactory().createTerm(term.op(),
                                                    newSubs.terms,
                                                    term.boundVars(),
                                                    term.javaBlock(),
                                                    labels);
    }

    @Override
    public String toString() {
        return "" + NAME + "(" + origin + ") (" + subtermOrigins + ")";
    }

    @Override
    public Name name() {
        return NAME;
    }

    @Override
    public Object getChild(int i) {
        if (i == 0) {
            return origin;
        } else if (i == 1) {
            return subtermOrigins;
        } else {
            return null;
        }
    }

    @Override
    public int getChildCount() {
        return CHILD_COUNT;
    }

    @Override
    public boolean isProofRelevant() {
        return false;
    }

    /**
     *
     * @return the term's origin.
     */
    public Origin getOrigin() {
        return origin;
    }

    /**
     * <p> Returns the origins of the term's sub-terms and former sub-terms. </p>
     *
     * <p> Note that you need to have called {@link #collectSubtermOrigins(Term, TermBuilder)}
     * for this method to work correctly. </p>
     *
     * @return the origins of the term's sub-terms and former sub-terms.
     * @see OriginTermLabelRefactoring
     */
    public Set<Origin> getSubtermOrigins() {
        return Collections.unmodifiableSet(subtermOrigins);
    }


    private static ImmutableArray<TermLabel>
                            computeOriginLabelsFromSubTermOrigins(final Term term,
                                                                  final Set<Origin> origins) {
        List<TermLabel> labels = term.getLabels().toList();
        final OriginTermLabel oldLabel = (OriginTermLabel) term.getLabel(NAME);

        if (oldLabel != null) {
            labels.remove(oldLabel);

            if ((!origins.isEmpty() || oldLabel.getOrigin().specType != SpecType.NONE)) {
                labels.add(new OriginTermLabel(
                        oldLabel.getOrigin(),
                        origins));
            }
        } else if (!origins.isEmpty()) {
            final OriginTermLabel newLabel =
                    new OriginTermLabel(computeCommonOrigin(origins), origins);

            labels.add(newLabel);
        }
        return new ImmutableArray<>(labels);
    }

    /**
     * @param subs the sub-terms to be searched
     * @param services a services object used for getting type information
     *                 and creating the new sub-term
     * @return origin information about the searched sub-terms stored in a
     *                {@link SubTermOriginData} object.
     */
    private static SubTermOriginData getSubTermOriginData(final ImmutableArray<Term> subs,
                                                          final Services services) {
        Term[] newSubs = new Term[subs.size()];
        Set<Origin> origins = new HashSet<>();

        for (int i = 0; i < newSubs.length; ++i) {
            newSubs[i] = collectSubtermOrigins(subs.get(i), services);
            final OriginTermLabel subLabel = (OriginTermLabel) newSubs[i].getLabel(NAME);

            if (subLabel != null) {
                origins.add(subLabel.getOrigin());
                origins.addAll(subLabel.getSubtermOrigins());
            }
        }
        return new SubTermOriginData(newSubs, origins);
    }

    /**
     * This class stores an array of sub-terms and a set of all their origins.
     * It is used when recursively collecting all origins from a term's sub-terms
     * for setting its respective origin labels. The information of the sub-terms
     * are used for propagating their origin label information upwards to their
     * enclosing term.
     *
     * @author Michael Kirsten
     *
     */
    private static class SubTermOriginData {
        /**  All collected sub-terms */
        public final Term[] terms;
        /** All collected origins */
        public final Set<Origin> origins;

        /**
         * This method constructs an object of type {@link SubTermOriginData}.
         * @param subterms the collected sub-terms
         * @param subtermOrigins the origin information collected from these sub-terms
         */
        public SubTermOriginData(Term[] subterms,
                                 Set<Origin> subtermOrigins) {
            this.terms = subterms;
            this.origins = subtermOrigins;
        }
    }

    /**
     * An origin encapsulates some information about where a term originates from.
     *
     * @author lanzinger
     */
    public static class Origin implements Comparable<Origin> {
        /**
         * The spec type the term originates from.
         */
        public final SpecType specType;

        /**
         * Creates a new {@link OriginTermLabel.Origin}.
         *
         * @param specType the spec type the term originates from.
         */
        public Origin(SpecType specType) {
            assert specType != null;

            this.specType = specType;
        }


        @Override
        public int compareTo(Origin other) {
            return Integer.compare(hashCode(), other.hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null
                    && obj.getClass().equals(getClass()) && ((Origin) obj).specType == specType;
        }

        @Override
        public int hashCode() {
            return specType.hashCode();
        }

        @Override
        public String toString() {
            return specType + " (implicit)";
        }
    }

    /**
     * Origin for terms that originate from a proof node.
     *
     * @author lanzinger
     */
    public static final class NodeOrigin extends Origin {

        /**
         * The name of the rule applied at the node the term originates from.
         */
        public final String ruleName;

        /**
         * The {@link Node#serialNr()} of the node the term originates from.
         */
        public final int nodeNr;

        /**
         * Creates a new {@link OriginTermLabel.Origin}.
         *
         * @param specType the spec type the term originates from.
         * @param ruleName the name of the rule applied at the node the term originates from.
         * @param nodeNr the {@link Node#serialNr()} of the node the term originates from.
         */
        public NodeOrigin(SpecType specType, String ruleName, int nodeNr) {
            super(specType);

            assert ruleName != null;

            this.ruleName = ruleName;
            this.nodeNr = nodeNr;
        }

        @Override
        public String toString() {
            return specType + " @ node " + nodeNr + " (" + ruleName + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            NodeOrigin other = (NodeOrigin) obj;
            if (nodeNr != other.nodeNr) {
                return false;
            }
            if (ruleName == null) {
                if (other.ruleName != null) {
                    return false;
                }
            } else if (!ruleName.equals(other.ruleName)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + nodeNr;
            result = prime * result + ((ruleName == null) ? 0 : ruleName.hashCode());
            return result;
        }
    }

    /**
     * Origin for terms that originate from a file.
     *
     * @author lanzinger
     */
    public static final class FileOrigin extends Origin {

        /**
         * The file the term originates from.
         */
        public final String fileName;

        /**
         * The line in the file the term originates from.
         */
        public final int line;

        /**
         * Creates a new {@link OriginTermLabel.Origin}.
         *
         * @param specType the spec type the term originates from.
         * @param fileName the file the term originates from.
         * @param line the line in the file.
         */
        public FileOrigin(SpecType specType, String fileName, int line) {
            super(specType);


            assert fileName != null;
            assert line >= 0;

            this.fileName = fileName;
            this.line = line;
        }

        @Override
        public String toString() {
            return specType + " @ file " + new File(fileName).getName() + " @ line " + line;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
            result = prime * result + line;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            FileOrigin other = (FileOrigin) obj;
            if (fileName == null) {
                if (other.fileName != null) {
                    return false;
                }
            } else if (!fileName.equals(other.fileName)) {
                return false;
            }
            if (line != other.line) {
                return false;
            }
            return true;
        }
    }

    /**
     * A {@code SpecType} is any type of JML specification which gets translated into JavaDL.
     *
     * @author lanzinger
     * @see OriginTermLabel.Origin
     */
    public static enum SpecType {

        /**
         * accessible
         */
        ACCESSIBLE("accessible"),

        /**
         * assignable
         */
        ASSIGNABLE("assignable"),

        /**
         * decreases
         */
        DECREASES("decreases"),

        /**
         * measured_by
         */
        MEASURED_BY("measured_by"),

        /**
         * invariant
         */
        INVARIANT("invariant"),

        /**
         * loop_invariant
         */
        LOOP_INVARIANT("loop_invariant"),

        /**
         * loop_invariant_free
         */
        LOOP_INVARIANT_FREE("loop_invariant_free"),

        /**
         * requires
         */
        REQUIRES("requires"),

        /**
         * requires_free
         */
        REQUIRES_FREE("requires_free"),

        /**
         * ensures
         */
        ENSURES("ensures"),

        /**
         * ensures_free
         */
        ENSURES_FREE("ensures_free"),

        /**
         * signals
         */
        SIGNALS("signals"),

        /**
         * signals_only
         */
        SIGNALS_ONLY("signals_only"),

        /**
         * breaks
         */
        BREAKS("breaks"),

        /**
         * continues
         */
        CONTINUES("continues"),

        /**
         * returns
         */
        RETURNS("returns"),

        /**
         * Interaction. Used for terms entered by the user.
         */
        INTERACTION("User_Interaction"),

        /**
         * None. Used when no other spec type fits and for terms whose origin was
         * not set upon their creation.
         */
        NONE("<none>");

        /**
         * This {@code SpecType}'s string representation.
         */
        private String name;

        /**
         * Creates a new {@code SpecType}
         *
         * @param name the {@code SpecType}'s string representation.
         */
        private SpecType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}