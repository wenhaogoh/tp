package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.main.SortBudgetCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.mainpageparser.SortBudgetCommandParser;
import seedu.address.model.budget.Threshold;
import seedu.address.model.expenditure.Price;
import seedu.address.model.sort.SortType;
import seedu.address.model.tag.Tag;
import seedu.address.state.budgetindex.BudgetIndex;
import seedu.address.state.budgetindex.BudgetIndexManager;
import seedu.address.state.expenditureindex.ExpenditureIndex;
import seedu.address.state.expenditureindex.ExpenditureIndexManager;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index has to be an integer greater than 0!";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses {@code oneBasedIndex} into a {@code ExpenditureIndex} and returns it.
     * Leading and trailing whitespaces will be trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static ExpenditureIndex parseExpenditureIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return new ExpenditureIndexManager(Integer.parseInt(trimmedIndex) - 1);
    }

    /**
     * Parses {@code args} into a {@code SortTypr} and returns it.
     * Leading and trailing whitespaces will be trimmed.
     * @throws ParseException if the specified Sort Type is invalid (unsupported sort types).
     */
    public static SortType parseSortType(String args) throws ParseException {
        String trimmedStringType = args.trim().toLowerCase();
        switch (trimmedStringType) {
        case "name": {
            return SortType.NAME;
        }

        case "time": {
            return SortType.TIME;
        }
        default: {
            throw new ParseException(String.format(SortBudgetCommandParser.MESSAGE_INVALID_SORT_TYPE + "\n"
                    + SortBudgetCommand.MESSAGE_USAGE));
        }
        }
    }


    /**
     * Parses {@code oneBasedIndex} into a {@code BudgetIndex} and returns it.
     * Leading and trailing whitespaces will be trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static BudgetIndex parseBudgetIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return new BudgetIndexManager(Integer.parseInt(trimmedIndex) - 1);
    }

    /**
     * Parses a {@code String expenditureName} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static seedu.address.model.expenditure.Name parseExpenditureName(String expenditureName)
            throws ParseException {
        requireNonNull(expenditureName);
        String trimmedName = expenditureName.trim();
        if (!seedu.address.model.expenditure.Name.isValidName(trimmedName)) {
            throw new ParseException(seedu.address.model.expenditure.Name.MESSAGE_CONSTRAINTS);
        } else if (trimmedName.length() > 50) {
            throw new ParseException("Expenditure Name is limited to 50 characters.");
        }
        return new seedu.address.model.expenditure.Name(trimmedName);
    }

    /**
     * Parses a {@code String price} into a {@code Price}.
     * Leading and trailing whitespaces will be trimmed.
     * @throws ParseException if the given {@code Price} is invalid.
     */
    public static Price parsePrice(String price) throws ParseException {
        requireNonNull(price);
        String trimmedPrice = price.trim();
        String parsedPrice = parseToDouble(trimmedPrice);
        if (!Price.isValid(parsedPrice)) {
            throw new ParseException(Price.MESSAGE_CONSTRAINTS);
        }
        if (Price.isZero(parsedPrice)) {
            throw new ParseException(Price.NON_ZERO_CONSTRAINTS);
        }
        if (Price.isExceededValue(parsedPrice)) {
            throw new ParseException(Price.EXCEEDED_VALUE_ERROR);
        }
        return new Price(trimmedPrice);
    }

    /**
     * Parses a {@code String budgetName} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code budgetName} is invalid.
     */
    public static seedu.address.model.budget.Name parseBudgetName(String budgetName) throws ParseException {
        requireNonNull(budgetName);
        String trimmedName = budgetName.trim();
        if (!seedu.address.model.budget.Name.isValid(trimmedName)) {
            throw new ParseException(seedu.address.model.budget.Name.MESSAGE_CONSTRAINTS);
        } else if (trimmedName.length() > 50) {
            throw new ParseException("Budget Name is limited to 50 characters.");
        }
        return new seedu.address.model.budget.Name(trimmedName);
    }

    /**
     * Parses a {@code String budgetThreshold} into a {@code Threshold}
     *
     * @throws ParseException if the given {@code budgetThreshold} is invalid.
     */
    public static Optional<Threshold> parseBudgetThreshold(String budgetThreshold) throws ParseException {
        requireNonNull(budgetThreshold);
        String trimmedThreshold = budgetThreshold.trim();
        String parsedThreshold = parseToDouble(trimmedThreshold);
        if (!Threshold.isValid(parsedThreshold)) {
            throw new ParseException(Threshold.MESSAGE_CONSTRAINTS);
        }
        if (Threshold.isZero(parsedThreshold)) {
            throw new ParseException(Threshold.NON_ZERO_CONSTRAINTS);
        }
        if (Threshold.isExceededValue(parsedThreshold)) {
            throw new ParseException(Threshold.EXCEEDED_VALUE_ERROR);
        }
        return new Threshold(parsedThreshold).toOptional();
    }

    private static String parseToDouble(String value) {
        assert ParserUtil.isDouble(value);
        return Double.toString(Double.parseDouble(value));
    }

    /**
     * Checks if input string is a valid double value or not.
     * Overflowed input values are considered as invalid.
     * @param input the input string.
     * @return a boolean on whether the string is a valid double or not.
     */

    public static boolean isValidDouble(String input) {
        final BigDecimal maxDouble = new BigDecimal(Double.MAX_VALUE);
        try {
            BigDecimal bigInput = new BigDecimal(input);
            return bigInput.compareTo(maxDouble) < 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if input string can be parsed to Double.
     * @param value input string.
     * @return boolean on whether input string can be parsed to Double or not.
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedLowerCaseTag = tag.trim().toLowerCase();
        if (!Tag.isValid(trimmedLowerCaseTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedLowerCaseTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            if (tagName.length() > 15) {
                throw new ParseException("Each tag is limited to 15 characters.");
            }
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }
}
