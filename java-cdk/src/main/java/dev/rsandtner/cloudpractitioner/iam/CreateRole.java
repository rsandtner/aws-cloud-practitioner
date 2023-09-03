package dev.rsandtner.cloudpractitioner.iam;

import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.constructs.Construct;

import java.util.List;

public class CreateRole extends Stack {

    public CreateRole(@Nullable Construct scope) {
        super(scope, "create-role");

        Role.Builder.create(this, "create-role")
                .roleName("demo-role")
                .assumedBy(new ServicePrincipal("ec2.amazonaws.com"))
                .managedPolicies(List.of(ManagedPolicy.fromManagedPolicyArn(this, "create-role-iam-access", "arn:aws:iam::aws:policy/IAMReadOnlyAccess")))
                .build();
    }
}
