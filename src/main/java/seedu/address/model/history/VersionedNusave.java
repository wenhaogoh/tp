package seedu.address.model.history;

import java.util.Optional;

import seedu.address.model.Nusave;
import seedu.address.model.budget.BudgetList;
import seedu.address.state.State;

public class VersionedNusave {
    private final Optional<Integer> budgetIndex;
    private final BudgetList budgetList;

    /**
     * Constructs a {@code VersionedNusave}.
     * @param state the current state of NUSave
     * @param nusave the NUSave model to be versioned
     */
    public VersionedNusave(State state, Nusave nusave) {
        this.budgetIndex = state.getBudgetIndex();
        this.budgetList = BudgetList.clone(nusave.getBudgetList());
    };

    public Optional<Integer> getBudgetIndex() {
        return budgetIndex;
    }

    public BudgetList getBudgetList() {
        return budgetList;
    }
}
