package dev.rsandtner.cloudpractitioner.budget;

import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.budgets.CfnBudget;
import software.amazon.awscdk.services.cloudwatch.ComparisonOperator;
import software.constructs.Construct;

import java.util.List;

public class FreeTierBudget extends Stack {

    private static final String BUDGET_NAME = "free-tier-budget";

    public FreeTierBudget(@Nullable Construct scope) {
        super(scope, BUDGET_NAME);

        CfnBudget.Builder.create(this, BUDGET_NAME)
                .budget(CfnBudget.BudgetDataProperty.builder()
                        .budgetName(BUDGET_NAME)
                        .budgetType("COST")
                        .budgetLimit(CfnBudget.SpendProperty.builder()
                                .amount(1.00)
                                .unit("USD")
                                .build())
                        .timeUnit("MONTHLY")
                        .costTypes(CfnBudget.CostTypesProperty.builder()
                                .includeTax(true)
                                .includeUpfront(true)
                                .includeRecurring(true)
                                .includeOtherSubscription(true)
                                .includeSupport(true)
                                .includeDiscount(true)
                                .includeRefund(false)
                                .includeCredit(false)
                                .build())
                        .build())
                .notificationsWithSubscribers(List.of(CfnBudget.NotificationWithSubscribersProperty.builder()
                        .notification(CfnBudget.NotificationProperty.builder()
                                .notificationType("ACTUAL")
                                .threshold(0.01)
                                .thresholdType("ABSOLUTE_VALUE")
                                .comparisonOperator("GREATER_THAN")
                                .build())
                        .subscribers(List.of(CfnBudget.SubscriberProperty.builder()
                                .subscriptionType("EMAIL")
                                .address("reinhard.sandtner@gmail.com")
                                .build()))
                        .build()))
                .build();
    }
}
