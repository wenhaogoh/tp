package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.budget.Threshold.NO_THRESHOLD_MESSAGE;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalBudget.KFC;
import static seedu.address.testutil.TypicalBudget.KFC_THRESHOLD;
import static seedu.address.testutil.TypicalBudget.MC_DONALDS;
import static seedu.address.testutil.TypicalBudget.SUBWAY;
import static seedu.address.testutil.TypicalBudget.SUBWAY_NAME;
import static seedu.address.testutil.TypicalBudget.getMcDonaldsBudget;
import static seedu.address.testutil.TypicalBudget.getSubwayBudget;
import static seedu.address.testutil.TypicalBudgets.getTypicalNusave;
import static seedu.address.testutil.TypicalExpenditure.KFC_TOTAL_PRICE;
import static seedu.address.testutil.TypicalExpenditure.KFC_ZINGER;
import static seedu.address.testutil.TypicalExpenditure.MC_MUFFIN;
import static seedu.address.testutil.TypicalExpenditure.SUBWAY_COLD_CUT_TRIO;
import static seedu.address.testutil.TypicalExpenditure.SUBWAY_COLD_CUT_TRIO_NAME;
import static seedu.address.testutil.TypicalExpenditure.SUBWAY_COOKIE;
import static seedu.address.testutil.TypicalExpenditure.SUBWAY_COOKIE_NAME;
import static seedu.address.testutil.TypicalExpenditure.SUBWAY_SOUP;
import static seedu.address.testutil.TypicalExpenditure.SUBWAY_TOTAL_PRICE;
import static seedu.address.testutil.TypicalExpenditure.getKfcZingerExpenditure;
import static seedu.address.testutil.TypicalExpenditure.getMcMuffinExpenditure;
import static seedu.address.testutil.TypicalExpenditure.getSubwayCookieExpenditure;
import static seedu.address.testutil.TypicalState.ENUM_PAGE_MAIN;
import static seedu.address.testutil.TypicalState.SUBWAY_PAGE_TITLE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.budget.Budget;
import seedu.address.model.budget.Threshold;
import seedu.address.state.Page;
import seedu.address.state.budgetindex.BudgetIndex;
import seedu.address.state.budgetindex.BudgetIndexManager;
import seedu.address.state.expenditureindex.ExpenditureIndex;
import seedu.address.state.expenditureindex.ExpenditureIndexManager;

public class ModelManagerTest {

    @Test
    public void constructor() {
        ModelManager modelManager = new ModelManager();
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new Nusave(), new Nusave(modelManager.getNusave()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        ModelManager modelManager = new ModelManager();
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    void setGuiSettings() {
        ModelManager modelManager = new ModelManager();
        GuiSettings guiSettings = new GuiSettings();
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    void getNusaveFilePath_validPath_getsNusaveFilePath() {
        ModelManager modelManager = new ModelManager();
        Path path = Paths.get("data", "nusave.json");
        assertEquals(path, modelManager.getNusaveFilePath());
    }

    @Test
    void setNusavePath_nullPath_throwsNullPointerException() {
        ModelManager modelManager = new ModelManager();
        assertThrows(NullPointerException.class, () -> modelManager.setNusavePath(null));
    }

    @Test
    void setNusavePath_validPath_setsNusavePath() {
        ModelManager modelManager = new ModelManager();
        Path path = Paths.get("address/book/file/path");
        modelManager.setNusavePath(path);
        assertEquals(path, modelManager.getNusaveFilePath());
    }

    @Test
    void setNusave_nullNusave_throwsNullPointerException() {
        ModelManager modelManager = new ModelManager();
        assertThrows(NullPointerException.class, () -> modelManager.setNusave(null));
    }


    @Test
    void setNusave_validNusave_setsNusave() {
        ModelManager modelManager = new ModelManager();
        ReadOnlyNusave nusave = new Nusave();
        modelManager.setNusave(nusave);
        assertEquals(nusave, modelManager.getNusave());
    }

    @Test
    void getNusave() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        ReadOnlyNusave nusaveToCheck = getTypicalNusave();
        ReadOnlyNusave nusave = modelManager.getNusave();
        assertEquals(nusaveToCheck, nusave);
    }

    @Test
    void openBudget_nullBudget_throwsAssertionError() {
        ModelManager modelManager = new ModelManager();
        BudgetIndex budgetIndex = new BudgetIndexManager(2);
        assertThrows(AssertionError.class, () -> modelManager.openBudget(budgetIndex));
    }

    @Test
    void openBudget_validOpenBudget_opensBudget() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        assertEquals(modelManager.getBudgetIndex().get(), 0);
        assertEquals(modelManager.getPage(), Page.BUDGET);
        assertTrue(modelManager.isBudgetPage());
    }

    @Test
    void closeBudget_validCloseBudget_closesBudget() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        modelManager.closeBudget();
        assertEquals(modelManager.getBudgetIndex(), Optional.empty());
        assertEquals(modelManager.getPage(), Page.MAIN);
        assertTrue(modelManager.isMain());
    }

    @Test
    void addBudget_nullBudget_throwsNullPointerException() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        assertThrows(NullPointerException.class, () -> modelManager.addBudget(null));
    }

    @Test
    void addBudget_validBudget_addsBudget() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        int size = modelManager.getNusave().getBudgetListAsObservableList().size();
        assertEquals(3, size);
    }

    @Test
    void deleteBudget_nullBudget_throwsAssertionError() {
        ModelManager modelManager = new ModelManager();
        assertThrows(AssertionError.class, () -> modelManager.deleteBudget(new BudgetIndexManager(0)));
    }

    @Test
    void deleteBudget_validBudget_deletesBudget() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.deleteBudget(new BudgetIndexManager(0));
        int size = modelManager.getNusave().getBudgetListAsObservableList().size();
        assertEquals(2, size);
    }

    @Test
    void editBudget_validBudget_deletesBudget() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        BudgetIndex indexToEdit = new BudgetIndexManager(0);
        Budget oldBudget = modelManager.getBudgetAtIndex(indexToEdit);
        Budget newBudget = SUBWAY;
        modelManager.editBudget(oldBudget, newBudget);
        assertEquals(newBudget, modelManager.getBudgetAtIndex(indexToEdit));
    }

    @Test
    void isIndexOutOfBound_outOfBoundIndex_true() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        assertTrue(modelManager.isIndexOutOfBound(new BudgetIndexManager(3)));
    }

    @Test
    void isIndexOutOfBound_withinBoundIndex_false() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        assertFalse(modelManager.isIndexOutOfBound(new BudgetIndexManager(0)));
    }

    @Test
    void deleteAllBudgets() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.deleteAllBudgets();
        assertEquals(0, modelManager.getNusave().getBudgetListAsObservableList().size());
    }

    @Test
    void findBudget_validInput() throws CommandException {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.findBudget(SUBWAY_NAME);
        assertEquals(1, modelManager.getFilteredRenderableList().size());
        assertEquals(SUBWAY, modelManager.getFilteredRenderableList().get(0));
    }

    @Test
    void findBudget_searchTermNoMatch() throws CommandException {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        assertThrows(CommandException.class, () -> modelManager.findBudget(SUBWAY_COLD_CUT_TRIO_NAME));
    }

    @Test
    void listBudget_validUse() throws CommandException {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.findBudget(SUBWAY_NAME);
        modelManager.listBudgets();
        assertEquals(SUBWAY, modelManager.getFilteredRenderableList().get(0));
        assertEquals(KFC, modelManager.getFilteredRenderableList().get(1));
        assertEquals(MC_DONALDS, modelManager.getFilteredRenderableList().get(2));
    }

    @Test
    void listBudget_noBudgets() throws CommandException {
        ModelManager modelManager = new ModelManager();
        assertThrows(CommandException.class, () -> modelManager.listBudgets());
    }

    @Test
    void sortBudgetsByName() throws CommandException {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());

        modelManager.addBudget(getSubwayBudget());
        modelManager.addBudget(getMcDonaldsBudget());
        modelManager.sortBudgetsByName();

        assertEquals(KFC, modelManager.getNusave().getBudgetListAsObservableList().get(0));
        assertEquals(MC_DONALDS, modelManager.getNusave().getBudgetListAsObservableList().get(1));
        assertEquals(MC_DONALDS, modelManager.getNusave().getBudgetListAsObservableList().get(2));
        assertEquals(SUBWAY, modelManager.getNusave().getBudgetListAsObservableList().get(3));
        assertEquals(SUBWAY, modelManager.getNusave().getBudgetListAsObservableList().get(4));
    }

    @Test
    void sortBudgetsByCreatedDate() throws CommandException {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());

        modelManager.addBudget(getMcDonaldsBudget());
        modelManager.sortBudgetsByCreatedDate();

        assertEquals(MC_DONALDS, modelManager.getNusave().getBudgetListAsObservableList().get(0));
        assertEquals(MC_DONALDS, modelManager.getNusave().getBudgetListAsObservableList().get(1));
        assertEquals(KFC, modelManager.getNusave().getBudgetListAsObservableList().get(2));
        assertEquals(SUBWAY, modelManager.getNusave().getBudgetListAsObservableList().get(3));
    }

    @Test
    void deleteExpenditure() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        modelManager.deleteExpenditure(new ExpenditureIndexManager(1));
        assertEquals(2, modelManager.getFilteredRenderableList().size());
        assertEquals(SUBWAY_SOUP, modelManager.getFilteredRenderableList().get(0));
        assertEquals(SUBWAY_COLD_CUT_TRIO, modelManager.getFilteredRenderableList().get(1));
    }

    @Test
    void addExpenditure() throws CommandException {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        modelManager.addExpenditure(getMcMuffinExpenditure());
        assertEquals(4, modelManager.getFilteredRenderableList().size());
        assertEquals(MC_MUFFIN, modelManager.getFilteredRenderableList().get(0));
        assertEquals(SUBWAY_SOUP, modelManager.getFilteredRenderableList().get(1));
        assertEquals(SUBWAY_COOKIE, modelManager.getFilteredRenderableList().get(2));
        assertEquals(SUBWAY_COLD_CUT_TRIO, modelManager.getFilteredRenderableList().get(3));
    }

    @Test
    void editExpenditure() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        modelManager.editExpenditure(getSubwayCookieExpenditure(), getKfcZingerExpenditure());
        assertEquals(3, modelManager.getFilteredRenderableList().size());
        assertEquals(SUBWAY_SOUP, modelManager.getFilteredRenderableList().get(0));
        assertEquals(KFC_ZINGER, modelManager.getFilteredRenderableList().get(1));
        assertEquals(SUBWAY_COLD_CUT_TRIO, modelManager.getFilteredRenderableList().get(2));
    }

    @Test
    void findExpenditure_validInput() throws CommandException {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        modelManager.findExpenditure(SUBWAY_COOKIE_NAME);
        assertEquals(1, modelManager.getFilteredRenderableList().size());
        assertEquals(SUBWAY_COOKIE, modelManager.getFilteredRenderableList().get(0));
    }

    @Test
    void findExpenditure_searchTermNoMatch() throws CommandException {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        assertThrows(CommandException.class, () -> modelManager.findExpenditure(SUBWAY_NAME));
    }

    @Test
    void listExpenditure_validUse() throws CommandException {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        modelManager.findExpenditure(SUBWAY_COOKIE_NAME);
        modelManager.listExpenditures();
        assertEquals(SUBWAY_SOUP, modelManager.getFilteredRenderableList().get(0));
        assertEquals(SUBWAY_COOKIE, modelManager.getFilteredRenderableList().get(1));
        assertEquals(SUBWAY_COLD_CUT_TRIO, modelManager.getFilteredRenderableList().get(2));
    }

    @Test
    void listExpenditures_noExpenditures() throws CommandException {
        ModelManager modelManager = new ModelManager();
        assertThrows(CommandException.class, () -> modelManager.listExpenditures());
    }

    @Test
    void sortExpendituresByName() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        modelManager.sortExpendituresByName();
        assertEquals(SUBWAY_SOUP, modelManager.getFilteredRenderableList().get(0));
        assertEquals(SUBWAY_COLD_CUT_TRIO, modelManager.getFilteredRenderableList().get(1));
        assertEquals(SUBWAY_COOKIE, modelManager.getFilteredRenderableList().get(2));

    }

    @Test
    void sortExpenditureByCreatedDate() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        modelManager.sortExpenditureByCreatedDate();
        assertEquals(SUBWAY_COLD_CUT_TRIO, modelManager.getFilteredRenderableList().get(0));
        assertEquals(SUBWAY_COOKIE, modelManager.getFilteredRenderableList().get(1));
        assertEquals(SUBWAY_SOUP, modelManager.getFilteredRenderableList().get(2));
    }

    @Test
    void calculateExpenditureValue() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        String expenditureValue =
                modelManager.calculateExpenditureValue(new BudgetIndexManager(modelManager.getBudgetIndex().get()));
        assertEquals(SUBWAY_TOTAL_PRICE, expenditureValue);
    }

    @Test
    void repopulateObservableList() {
    }

    @Test
    void isMain() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        assertEquals(true, modelManager.isMain());
    }

    @Test
    void isBudget() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        assertEquals(false, modelManager.isBudget());
    }

    @Test
    void isWithinRange_validBudgetIndex_true() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        BudgetIndex validBudgetIndex = new BudgetIndexManager(2);
        assertEquals(true, modelManager.isWithinRange(validBudgetIndex));
    }

    @Test
    void isWithinRange_budgetIndexIsMoreThanListSize_false() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        BudgetIndex invalidBudgetIndex = new BudgetIndexManager(3);
        assertEquals(false, modelManager.isWithinRange(invalidBudgetIndex));
    }

    @Test
    void isWithinRange_validExpenditureIndex_true() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        ExpenditureIndex expenditureIndex = new ExpenditureIndexManager(1);
        assertTrue(modelManager.isWithinRange(expenditureIndex));
    }

    @Test
    void isWithinRange_expenditureIndexIsMoreThanListSize_false() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        ExpenditureIndex expenditureIndex = new ExpenditureIndexManager(5);
        assertFalse(modelManager.isWithinRange(expenditureIndex));
    }

    @Test
    void getPage() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        assertEquals(ENUM_PAGE_MAIN, modelManager.getPage());
    }

    @Test
    void getPageName() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        assertEquals(SUBWAY_NAME, modelManager.getPageName(new BudgetIndexManager(0)));
    }

    @Test
    void getPageTitle() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        assertEquals(SUBWAY_PAGE_TITLE, modelManager.getPageName(new BudgetIndexManager(0)));
    }

    @Test
    void getTotalExpenditureValue() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(1));
        assertEquals(KFC_TOTAL_PRICE, modelManager.getTotalExpenditureValue());
    }

    @Test
    void getThreshold() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(1));
        assertEquals(new Threshold(KFC_THRESHOLD), modelManager.getThreshold().get());
    }

    @Test
    void isBudgetPageTest_isMainPage_false() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        assertFalse(modelManager.isBudgetPage());
    }

    @Test
    void isBudgetPageTest_isBudgetPage_true() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(2));
        assertTrue(modelManager.isBudgetPage());
    }

    @Test
    void setBudgetIndex() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.setBudgetIndex(new BudgetIndexManager(1));
        assertEquals(1, modelManager.getBudgetIndex().get());
    }

    @Test
    void setPage() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.setPage(Page.MAIN);
        assertEquals(ENUM_PAGE_MAIN, modelManager.getPage());
    }

    @Test
    void setThreshold_mainPageValidInput_success() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.setThreshold(Optional.empty());
        assertEquals(NO_THRESHOLD_MESSAGE, modelManager.getThresholdStringProp().getValue());
    }

    @Test
    void setThreshold_mainPageInvalidInput_throwsAssertionError() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        assertThrows(AssertionError.class, () -> modelManager.setThreshold(new Threshold("100").toOptional()));
    }

    @Test
    void setThreshold_budgetPageValidInput_success() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        modelManager.setThreshold(new Threshold("100").toOptional());
        assertEquals("100", modelManager.getThresholdStringProp().getValue());
    }

    @Test
    void setThreshold_budgetPageInvalidInput_throwsAssertionError() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        assertThrows(AssertionError.class, () -> modelManager.setThreshold(Optional.empty()));
    }

    @Test
    void setPageTitle() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.setPageTitle(getSubwayBudget().getName().value);
        assertEquals(SUBWAY_PAGE_TITLE, modelManager.getPageTitle());
    }

    @Test
    void setTotalExpenditure() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        modelManager.openBudget(new BudgetIndexManager(0));
        modelManager.deleteExpenditure(new ExpenditureIndexManager(0));
        assertEquals("9.00", modelManager.getTotalExpenditureValue());
    }

    @Test
    void getFilteredRenderableList() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        assertEquals(SUBWAY, modelManager.getFilteredRenderableList().get(0));
        assertEquals(KFC, modelManager.getFilteredRenderableList().get(1));
        assertEquals(MC_DONALDS, modelManager.getFilteredRenderableList().get(2));
    }

    @Test
    void updateFilteredRenderableList() {
    }

    @Test
    void equals_sameObject_true() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        assertEquals(true, modelManager.equals(modelManager));
    }

    @Test
    void equals_null_false() {
        ModelManager modelManager = new ModelManager(getTypicalNusave(), new UserPrefs());
        assertEquals(false, modelManager.equals(null));
    }

}
