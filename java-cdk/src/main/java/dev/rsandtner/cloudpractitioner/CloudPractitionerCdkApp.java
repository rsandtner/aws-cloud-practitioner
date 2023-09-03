package dev.rsandtner.cloudpractitioner;

import dev.rsandtner.cloudpractitioner.budget.FreeTierBudget;
import dev.rsandtner.cloudpractitioner.iam.CreateRole;
import software.amazon.awscdk.App;

public class CloudPractitionerCdkApp {

    public static void main(String[] args) {

        var app = new App();

        new FreeTierBudget(app);
        new CreateRole(app);

        app.synth();
    }
}
