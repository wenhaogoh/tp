package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.budget.Budget;
import seedu.address.model.budget.BudgetList;
import seedu.address.model.budget.Threshold;
import seedu.address.model.expenditure.Expenditure;
import seedu.address.state.State;
import seedu.address.state.budgetindex.BudgetIndex;

public class Nusave implements ReadOnlyNusave {
    private final BudgetList budgetList;
    private final ObservableList<Renderable> internalList = FXCollections.observableArrayList();
    private final ObservableList<Renderable> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);
    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        this.budgetList = new BudgetList();
    }

    public Nusave() { }

    /**
     *
     * @param toBeCopied
     */
    public Nusave(ReadOnlyNusave toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    public String getPageName(BudgetIndex index) {
        return this.budgetList.getBudgetName(index);
    }

    /**
     * Resets the data with the new NUsave
     * @param newData
     */
    public void resetData(ReadOnlyNusave newData) {
        requireNonNull(newData);

        setBudgets(newData.getBudgetListAsObservableList());
    }

    //=========== Budget ==================================================================================
    /**
     * Adds a budget to the NUSave.
     */
    public void addBudgetToFront(Budget budget) {
        this.budgetList.addToFront(budget);
        this.internalList.add(0, budget);
    }

    /**
     * Adds a budget to the NUSave.
     * @param budget the budget to be added.
     */
    public void addBudget(Budget budget) {
        this.budgetList.add(budget);
        this.internalList.add(budget);
    }

    /**
     * Deletes a budget from NUSave.
     */
    public void deleteBudget(Budget toRemove) {
        this.budgetList.remove(toRemove);
        this.internalList.remove(toRemove);
    }

    /**
     * Edit a budget from NUSave.
     * @param oldBudget the budget to be edited
     * @param editedBudget edited budget to replace the old budget
     */
    public void editBudget(Budget oldBudget, Budget editedBudget) {
        int index = budgetList.getIndexOfBudget(oldBudget);
        this.budgetList.editBudget(oldBudget, editedBudget);
        this.internalList.set(index, editedBudget);
    }

    public void setBudgets(BudgetList budgetList) {
        List<Budget> budgets = budgetList.getBudgets();
        setBudgets(budgets);
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgetList.setBudgets(budgets);
        this.internalList.setAll(budgets);
    }

    public BudgetList getBudgetList() {
        return budgetList;
    }

    public Optional<Threshold> getThreshold(Optional<Integer> budgetIndexOpt) {
        assert budgetIndexOpt.isPresent();
        int budgetIndex = budgetIndexOpt.get();
        return this.budgetList.getBudgetThreshold(budgetIndex);
    }

    public void deleteAllBudgets() {
        setBudgets(new ArrayList<>());
    }

    public int getIndexOfBudget(Budget budget) {
        return budgetList.getIndexOfBudget(budget);
    }

    public boolean isBudgetOverLimit() {
        return budgetList.getSize() > 100;
    }

    //=========== Expenditure ==================================================================================
    /**
     * Adds a expenditure to the NUSave budget according to its index.
     */
    public void addExpenditure(Expenditure expenditure, Optional<Integer> budgetIndexOpt) {
        assert budgetIndexOpt.isPresent();
        int budgetIndex = budgetIndexOpt.get();
        this.internalList.add(0, expenditure);
        this.budgetList.addExpenditure(expenditure, budgetIndex);
    }

    /**
     * Edits an expenditure to the NUSave budget according to its index.
     * @param oldExpenditure
     * @param editedExpenditure
     * @param budgetIndexOpt
     */
    public void editExpenditure(Expenditure oldExpenditure, Expenditure editedExpenditure,
                                Optional<Integer> budgetIndexOpt) {
        assert budgetIndexOpt.isPresent();
        int budgetIndex = budgetIndexOpt.get();
        int expenditureIndex = this.internalList.indexOf(oldExpenditure);
        this.internalList.set(expenditureIndex, editedExpenditure);
        this.budgetList.editExpenditure(oldExpenditure, editedExpenditure, budgetIndex);
    }

    /**
     * Deletes an expenditure from the NUSave budget according to its index.
     */
    public void deleteExpenditure(Expenditure expenditure, Optional<Integer> budgetIndexOpt) {
        assert budgetIndexOpt.isPresent();
        int budgetIndex = budgetIndexOpt.get();
        List<Expenditure> expenditureList = budgetList.getExpendituresAsList(budgetIndex);
        expenditureList.remove(expenditure);
        this.internalList.remove(expenditure);
    }

    public String getTotalExpenditureValue(Optional<Integer> budgetIndexOpt) {
        assert budgetIndexOpt.isPresent();
        int budgetIndex = budgetIndexOpt.get();
        return this.budgetList.getTotalExpenditureValue(budgetIndex);
    }

    public boolean isExpenditureOverLimit(Optional<Integer> budgetIndexOpt) {
        assert budgetIndexOpt.isPresent();
        int budgetIndex = budgetIndexOpt.get();
        int expenditureSize = budgetList.getExpenditures(budgetIndex).getExpenditureSize();
        return expenditureSize > 99;
    }

    //=========== ObservableList ==================================================================================
    /**
     * Sets the observable list displayed in UI based on the
     * current state.
     * @param state State containing the current page and
     *              budget index.
     */
    public void repopulateObservableList(State state) {
        if (state.isBudget()) {
            // repopulate observable list with Expenditures
            assert state.getBudgetIndex().isPresent();
            int index = state.getBudgetIndex().get();
            assert index < budgetList.getSize();
            this.internalList.setAll(budgetList.getExpendituresAsList(index));
        } else if (state.isMain()) {
            // repopulate observable list with Budgets
            this.internalList.setAll(budgetList.getBudgets());
        }
    }

    @Override
    public ObservableList<Budget> getBudgetListAsObservableList() {
        return budgetList.asUnmodifiableObservableList();
    }

    public ObservableList<Renderable> getInternalList() {
        return internalUnmodifiableList;
    }

    public void sortBudgetListByName() {
        budgetList.sortBudgetsListByName();
    }

    public void sortBudgetListByCreatedDate() {
        this.budgetList.sortBudgetListByCreatedDate();
    }

    /**
     * sort current ExpenditureList by Name
     * @param state current state
     */
    public void sortExpendituresByName(State state) {
        assert state.getBudgetIndex().isPresent();
        int index = state.getBudgetIndex().get();
        budgetList.getExpenditures(index).sortExpendituresByName();
    }

    /**
     * sort current ExpenditureList by CreateDate
     * @param state current state
     */
    public void sortExpendituresByCreateDate(State state) {
        assert state.getBudgetIndex().isPresent();
        int index = state.getBudgetIndex().get();
        budgetList.getExpenditures(index).sortExpendituresByCreateDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Nusave nusave = (Nusave) o;
        return Objects.equals(budgetList, nusave.budgetList)
                && Objects.equals(internalList, nusave.internalList)
                && Objects.equals(internalUnmodifiableList, nusave.internalUnmodifiableList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(budgetList, internalList, internalUnmodifiableList);
    }

}
