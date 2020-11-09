package seedu.address.logic.parser.mainpageparser;

import seedu.address.logic.commands.main.FindBudgetCommand;
import seedu.address.logic.parser.Parser;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.budget.Name;

/**
 * Represents a Parser that is able to parse user inputs regarding finding expenditures.
 */

public class FindBudgetCommandParser implements Parser<FindBudgetCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the FindExpenditureCommand
     * and returns a FindExpenditureCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public FindBudgetCommand parse(String userInput) throws ParseException {
        Name name = ParserUtil.parseBudgetName(userInput);
        return new FindBudgetCommand(name);
    }
}
