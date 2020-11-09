package seedu.address.logic.parser.budgetpageparser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_MORE_THAN_THREE_TAGS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.budget.EditExpenditureCommand;
import seedu.address.logic.commands.budget.EditExpenditureCommand.EditExpenditureDescriptor;
import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.ArgumentTokenizer;
import seedu.address.logic.parser.Parser;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.Prefix;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;
import seedu.address.state.expenditureindex.ExpenditureIndex;

public class EditExpenditureCommandParser implements Parser<EditExpenditureCommand> {

    @Override
    public EditExpenditureCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PRICE, PREFIX_TAG);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME)
                && !arePrefixesPresent(argMultimap, PREFIX_PRICE) && !arePrefixesPresent(argMultimap, PREFIX_TAG)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditExpenditureCommand.MESSAGE_USAGE));
        }

        ExpenditureIndex index;

        index = ParserUtil.parseExpenditureIndex(argMultimap.getPreamble());


        EditExpenditureDescriptor editExpenditureDescriptor = new EditExpenditureDescriptor();
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editExpenditureDescriptor.setName(
                    ParserUtil.parseExpenditureName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PRICE).isPresent()) {
            editExpenditureDescriptor.setPrice(
                    ParserUtil.parsePrice(argMultimap.getValue(PREFIX_PRICE).get()));
        }

        Set<Tag> tags = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        if (tags.size() > 3) {
            throw new ParseException(MESSAGE_MORE_THAN_THREE_TAGS);
        }

        editExpenditureDescriptor.setTags(tags);

        return new EditExpenditureCommand(index, editExpenditureDescriptor);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
