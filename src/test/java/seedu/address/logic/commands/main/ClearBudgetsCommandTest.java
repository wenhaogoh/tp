package seedu.address.logic.commands.main;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.testutil.TypicalBudget;

class ClearBudgetsCommandTest {
    @Test
    public void execute_emptyNusave_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearBudgetsCommand(), model, ClearBudgetsCommand.MESSAGE_DELETE_BUDGET_SUCCESS,
                expectedModel);
    }

    @Test
    public void execute_nonEmptyNusave_success() throws CommandException {
        Model model = new ModelManager();
        model.addBudget(TypicalBudget.getMcDonaldsBudget());
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearBudgetsCommand(), model, ClearBudgetsCommand.MESSAGE_DELETE_BUDGET_SUCCESS,
                expectedModel);
    }
}
