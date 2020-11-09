package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.budget.Threshold.NO_THRESHOLD_MESSAGE;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.budget.FindExpenditureCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.main.FindBudgetCommand;
import seedu.address.model.budget.Budget;
import seedu.address.model.budget.BudgetList;
import seedu.address.model.budget.Threshold;
import seedu.address.model.expenditure.Expenditure;
import seedu.address.model.history.History;
import seedu.address.model.history.HistoryManager;
import seedu.address.model.history.VersionedNusave;
import seedu.address.state.Page;
import seedu.address.state.PageTitle;
import seedu.address.state.State;
import seedu.address.state.StateManager;
import seedu.address.state.budgetindex.BudgetIndex;
import seedu.address.state.budgetindex.BudgetIndexManager;
import seedu.address.state.budgetindex.EmptyBudgetIndex;
import seedu.address.state.expenditureindex.ExpenditureIndex;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final Nusave nusave;
    private final UserPrefs userPrefs;
    private final FilteredList<Renderable> filteredRenderables;
    private final State state;
    private final History<VersionedNusave> history;

    /**
     * Initializes a ModelManager with the given nusave and userPrefs.
     */
    public ModelManager(ReadOnlyNusave nusave, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(nusave, userPrefs);
        logger.fine("Initializing with NUSave: " + nusave + " and user prefs " + userPrefs);
        this.nusave = new Nusave(nusave);
        this.userPrefs = new UserPrefs(userPrefs);
        this.filteredRenderables = new FilteredList<>(this.nusave.getInternalList());
        this.state = new StateManager(new EmptyBudgetIndex(), Page.MAIN, PageTitle.MAIN_PAGE_TITLE);
        this.history = new HistoryManager<>();
    }
    /**
     * Initializes a new ModelManager.
     */
    public ModelManager() {
        this(new Nusave(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getNusaveFilePath() {
        return userPrefs.getNusaveFilePath();
    }

    @Override
    public void setNusavePath(Path nusaveFilePath) {
        requireNonNull(nusaveFilePath);
        userPrefs.setNusaveFilePath(nusaveFilePath);
    }

    //=========== Nusave =======

    @Override
    public void setNusave(ReadOnlyNusave nusave) {
        requireNonNull(nusave);
        this.nusave.resetData(nusave);
    }

    @Override
    public ReadOnlyNusave getNusave() {
        return nusave;
    }

    //=========== Budgets =======

    /**
     * Opens a budget from NUSave.
     */
    private void openBudget(Integer index) {
        requireNonNull(index);
        BudgetIndex budgetIndex = new BudgetIndexManager(index);
        openBudget(budgetIndex);
    }

    /**
     * Open a budget from NUSave.
     * @param budgetIndex the index of the budget to be opened
     */
    @Override
    public void openBudget(BudgetIndex budgetIndex) {
        requireNonNull(budgetIndex);
        setOpenCommandState(budgetIndex);
        displayAllRenderables();
        repopulateObservableList();
    }

    private void setOpenCommandState(BudgetIndex budgetIndex) {
        BudgetIndex actualBudgetIndex = getActualBudgetIndex(budgetIndex);
        state.setBudgetIndex(actualBudgetIndex);
        String pageName = getPageName(actualBudgetIndex);
        String newExpenditureValue = calculateExpenditureValue(actualBudgetIndex);
        Optional<Threshold> newThreshold = getThreshold();
        state.setOpenCommandState(pageName, newExpenditureValue, newThreshold);
    }

    @Override
    public void closeBudget() {
        if (state.getPage() == Page.MAIN) {
            return;
        }
        setCloseCommandState();
        displayAllRenderables();
        repopulateObservableList();
    }

    private void setCloseCommandState() {
        state.setCloseCommandState();
    }

    private BudgetIndex getActualBudgetIndex(BudgetIndex budgetIndex) {
        assert budgetIndex.getBudgetIndex().isPresent();
        int indexInFilteredList = budgetIndex.getBudgetIndex().get();
        assert indexInFilteredList >= 0 && indexInFilteredList < filteredRenderables.size();
        Budget budget = (Budget) filteredRenderables.get(indexInFilteredList);
        int indexInBudgetList = nusave.getIndexOfBudget(budget);
        return new BudgetIndexManager(indexInBudgetList);
    }


    /**
     * Adds a budget to NUSave.
     * @param budget the budget to be added
     */
    @Override
    public void addBudget(Budget budget) throws CommandException {
        requireNonNull(budget);
        if (nusave.isBudgetOverLimit()) {
            throw new CommandException("You are not allowed to have more than 100 budgets");
        }
        nusave.addBudgetToFront(budget);
        displayAllRenderables();
    }

    /**
     * Deletes a budget in NUSave.
     * @param budgetIndex the index of the budget to be deleted.
     */
    @Override
    public void deleteBudget(BudgetIndex budgetIndex) {
        requireNonNull(budgetIndex);
        assert budgetIndex.getBudgetIndex().isPresent();
        int index = budgetIndex.getBudgetIndex().get();
        assert index < filteredRenderables.size();
        Budget budget = (Budget) filteredRenderables.get(index);
        nusave.deleteBudget(budget);
        displayAllRenderables();
    }

    /**
     * Edit a budget from NUSave.
     * @param oldBudget the budget to be edited
     * @param editedBudget edited budget to replace the old budget
     */
    @Override
    public void editBudget(Budget oldBudget, Budget editedBudget) {
        requireAllNonNull(oldBudget, editedBudget);
        nusave.editBudget(oldBudget, editedBudget);
        displayAllRenderables();
    }

    @Override
    public Budget getBudgetAtIndex(BudgetIndex budgetIndex) {
        return (Budget) filteredRenderables.get(budgetIndex.getBudgetIndex().get());
    }

    @Override
    public Expenditure getExpenditureAtIndex(ExpenditureIndex expenditureIndex) {
        return (Expenditure) filteredRenderables.get(expenditureIndex.getExpenditureIndex().get());
    }

    @Override
    public boolean isIndexOutOfBound(BudgetIndex budgetIndex) {
        return budgetIndex.getBudgetIndex().get() >= filteredRenderables.size();
    }

    public boolean isIndexOutOfBound(ExpenditureIndex expenditureIndex) {
        return expenditureIndex.getExpenditureIndex().get() >= filteredRenderables.size();
    }

    @Override
    public void deleteAllBudgets() {
        nusave.deleteAllBudgets();
    }

    /**
     * Searches for budgets in NUSave that contains the search term.
     * @param searchTerm the search term to be used.
     */
    @Override
    public void findBudget(String searchTerm) throws CommandException {
        Predicate<Renderable> predicate = renderable -> renderable.contains(searchTerm);
        updateFilteredRenderableList(predicate);
        if (filteredRenderables.size() == 0) {
            throw new CommandException(String.format(FindBudgetCommand.MESSAGE_NO_BUDGETS_FOUND, searchTerm));
        }
    }

    /**
     * Lists all budgets stored in NUSave.
     */
    @Override
    public void listBudgets() throws CommandException {
        displayAllRenderables();
        if (filteredRenderables.size() == 0) {
            throw new CommandException("You have no budgets recorded, try creating one with the create command!");
        }
    }

    /**
     * Sorts all budgets stored in NUSave by alphanumerical order.
     */
    @Override
    public void sortBudgetsByName() {
        nusave.sortBudgetListByName();
        repopulateObservableList();
    }

    /**
     * Sorts all budgets stored in NUSave based on its created date.
     */
    @Override
    public void sortBudgetsByCreatedDate() {
        nusave.sortBudgetListByCreatedDate();
        repopulateObservableList();
    }

    //=========== Expenditures =======

    /**
     * Deletes an expenditure from the specified budget and updates the list.
     * @param expenditureIndex the index expenditure to be deleted
     */
    @Override
    public void deleteExpenditure(ExpenditureIndex expenditureIndex) {
        requireNonNull(expenditureIndex);
        assert expenditureIndex.getExpenditureIndex().isPresent();
        int index = expenditureIndex.getExpenditureIndex().get();
        assert index < filteredRenderables.size();
        Expenditure expenditure = (Expenditure) filteredRenderables.get(index);
        Optional<Integer> budgetIndex = state.getBudgetIndex();
        nusave.deleteExpenditure(expenditure, budgetIndex);
        setTotalExpenditure(nusave.getTotalExpenditureValue(budgetIndex));
        displayAllRenderables();
    }

    /**
     * Adds an expenditure to the specified budget.
     * @param expenditure the expenditure to be added
     */
    @Override
    public void addExpenditure(Expenditure expenditure) throws CommandException {
        requireNonNull(expenditure);
        Optional<Integer> budgetIndex = this.state.getBudgetIndex();
        if (nusave.isExpenditureOverLimit(budgetIndex)) {
            throw new CommandException("You are not allowed to have more than 100 expenditures");
        }
        nusave.addExpenditure(expenditure, this.state.getBudgetIndex());
        setTotalExpenditure(nusave.getTotalExpenditureValue(budgetIndex));
        displayAllRenderables();
    }

    /**
     * Edit an expenditure in the specified budget.
     * @param oldExpenditure the expenditure to be edited
     * @param editedExpenditure edited expenditure to replace the old expenditure
     */
    @Override
    public void editExpenditure(Expenditure oldExpenditure, Expenditure editedExpenditure) {
        requireAllNonNull(oldExpenditure, editedExpenditure);
        Optional<Integer> budgetIndex = this.state.getBudgetIndex();
        nusave.editExpenditure(oldExpenditure, editedExpenditure, budgetIndex);
        setTotalExpenditure(nusave.getTotalExpenditureValue(budgetIndex));
        displayAllRenderables();
    }

    /**
     * Lists all expenditures in the specified budget.
     */
    @Override
    public void listExpenditures() throws CommandException {
        displayAllRenderables();
        if (filteredRenderables.size() == 0) {
            throw new CommandException("You have no expenditures recorded, try creating one with the add command!");
        }
    }

    /**
     * Searches for expenditures in the specified budget that contains the search term.
     * @param searchTerm the search term to be used.
     */
    @Override
    public void findExpenditure(String searchTerm) throws CommandException {
        Predicate<Renderable> predicate = renderable -> renderable.contains(searchTerm);
        updateFilteredRenderableList(predicate);
        if (filteredRenderables.size() == 0) {
            throw new CommandException(String.format(FindExpenditureCommand.MESSAGE_NO_EXPENDITURES_FOUND, searchTerm));
        }
    }

    /**
     * Sorts all expenditures stored in the specified budget by alphanumerical order.
     */
    @Override
    public void sortExpendituresByName() {
        nusave.sortExpendituresByName(state);
        repopulateObservableList();
    }

    /**
     * Sorts all expenditures stored in the specified budget by created date.
     */
    @Override
    public void sortExpenditureByCreatedDate() {
        nusave.sortExpendituresByCreateDate(state);
        repopulateObservableList();
    }

    @Override
    public String calculateExpenditureValue(BudgetIndex budgetIndex) {
        Optional<Integer> indexOpt = budgetIndex.getBudgetIndex();
        return nusave.getTotalExpenditureValue(indexOpt);
    }

    //=========== ObservableList =======

    @Override
    public void repopulateObservableList() {
        nusave.repopulateObservableList(state);
    }

    //=========== StateManager ================================================================================

    @Override
    public boolean isMain() {
        return this.state.isMain();
    }

    @Override
    public boolean isBudget() {
        return this.state.isBudget();
    }

    @Override
    public boolean isWithinRange(BudgetIndex budgetIndex) {
        assert budgetIndex.getBudgetIndex().isPresent();
        int index = budgetIndex.getBudgetIndex().get();
        return index < filteredRenderables.size();
    }

    @Override
    public boolean isWithinRange(ExpenditureIndex expenditureIndex) {
        assert expenditureIndex.getExpenditureIndex().isPresent();
        int index = expenditureIndex.getExpenditureIndex().get();
        return index < filteredRenderables.size();
    }

    @Override
    public Page getPage() {
        return this.state.getPage();
    }

    @Override
    public BooleanProperty getBudgetPageProp() {
        return this.state.getIsBudgetPageProp();
    }

    @Override
    public StringProperty getTotalExpenditureStringProp() {
        return state.getInfoBoxSecondRowProp();
    }

    @Override
    public StringProperty getThresholdStringProp() {
        return state.getThresholdStringProp();
    }

    @Override
    public String getPageName(BudgetIndex index) {
        return this.nusave.getPageName(index);
    }

    @Override
    public String getPageTitle() {
        return this.state.getPageTitle();
    }

    @Override
    public String getTotalExpenditureValue() {
        Optional<Integer> budgetIndex = state.getBudgetIndex();
        assert budgetIndex.isPresent();
        return nusave.getTotalExpenditureValue(budgetIndex);
    }

    @Override
    public Optional<Threshold> getThreshold() {
        Optional<Integer> budgetIndex = state.getBudgetIndex();
        return nusave.getThreshold(budgetIndex);
    }

    @Override
    public boolean isBudgetPage() {
        return this.state.isBudgetPage();
    }

    @Override
    public void setBudgetIndex(BudgetIndex budgetIndex) {
        assert budgetIndex != null;
        this.state.setBudgetIndex(budgetIndex);
    }

    public Optional<Integer> getBudgetIndex() {
        return this.state.getBudgetIndex();
    }

    @Override
    public void setPage(Page page) {
        assert page != null;
        this.state.setPage(page);
    }

    @Override
    public void setTotalExpenditure(String expenditure) {
        this.state.setTotalExpenditure(expenditure);
    }

    @Override
    public void setThreshold(Optional<Threshold> threshold) {
        if (threshold.isPresent()) {
            assert getPage() == Page.BUDGET;
            String thresholdStr = threshold.get().toString();
            this.state.setThresholdStringProp(thresholdStr);
        } else {
            assert getPage() == Page.MAIN;
            this.state.setThresholdStringProp(NO_THRESHOLD_MESSAGE);
        }
    }

    @Override
    public void setPageTitle(String page) {
        this.state.setPageTitle(page);
    }

    //=========== Filtered Renderable List Accessors =============================================================

    @Override
    public ObservableList<Renderable> getFilteredRenderableList() {
        return filteredRenderables;
    }

    private void displayAllRenderables() {
        updateFilteredRenderableList(PREDICATE_SHOW_ALL_RENDERABLES);
    }

    @Override
    public void updateFilteredRenderableList(Predicate<Renderable> predicate) {
        requireNonNull(predicate);
        filteredRenderables.setPredicate(predicate);
    }

    //=========== Undo Redo =============================================================

    @Override
    public boolean canUndo() {
        return history.hasHistory();
    }

    @Override
    public void undo() {
        assert canUndo();
        saveToFuture();
        VersionedNusave previousVersion = history.getHistory();
        loadVersionedNusave(previousVersion);
    }

    @Override
    public boolean canRedo() {
        return history.hasFuture();
    }

    @Override
    public void redo() {
        assert canRedo();
        VersionedNusave futureVersion = history.getFuture();
        loadVersionedNusave(futureVersion);
    }

    /**
     * Saves a deep copy of the current nusave into the history.
     */
    @Override
    public void saveToHistory() {
        VersionedNusave toSave = new VersionedNusave(state, nusave);
        history.saveToHistory(toSave);
        logger.info("Saving a version of NUSave...");
    }

    private void saveToFuture() {
        VersionedNusave toSave = new VersionedNusave(state, nusave);
        history.saveToFuture(toSave);
        logger.info("Saving a version of NUSave...");
    }

    private void loadVersionedNusave(VersionedNusave versionedNusave) {
        BudgetList budgetList = versionedNusave.getBudgetList();
        nusave.setBudgets(budgetList);
        Optional<Integer> budgetIndex = versionedNusave.getBudgetIndex();
        if (budgetIndex.isEmpty()) {
            closeBudget();
        } else {
            Integer index = budgetIndex.get();
            openBudget(index);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ModelManager that = (ModelManager) o;
        return Objects.equals(nusave, that.nusave)
                && Objects.equals(userPrefs, that.userPrefs)
                && Objects.equals(filteredRenderables, that.filteredRenderables)
                && Objects.equals(state, that.state);
    }
}
