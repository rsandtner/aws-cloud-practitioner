package dev.rsandtner.cloudpractitioner.ec2;

import dev.rsandtner.cloudpractitioner.Env;
import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Size;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.AmazonLinux2023ImageSsmParameterProps;
import software.amazon.awscdk.services.ec2.AmazonLinux2023Kernel;
import software.amazon.awscdk.services.ec2.AmazonLinuxCpuType;
import software.amazon.awscdk.services.ec2.CfnKeyPair;
import software.amazon.awscdk.services.ec2.EbsDeviceVolumeType;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.Instance;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.MachineImage;
import software.amazon.awscdk.services.ec2.Peer;
import software.amazon.awscdk.services.ec2.Port;
import software.amazon.awscdk.services.ec2.SecurityGroup;
import software.amazon.awscdk.services.ec2.UserData;
import software.amazon.awscdk.services.ec2.Volume;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcLookupOptions;
import software.constructs.Construct;

import java.util.stream.IntStream;

public class SimpleEc2Stack extends Stack {

    public SimpleEc2Stack(@Nullable Construct scope) {
        super(scope, "simple-ec2-stack", StackProps.builder()
                .env(Env.get())
                .build());

        var ipAddress = "83.65.159.44/32";
        var vpc = Vpc.fromLookup(this, "default-vpc", VpcLookupOptions.builder()
                .isDefault(true)
                .build());

        var sshSecurityGroup = createSecurityGroup("ssh-security-group", ipAddress, vpc, 22);
        var httpSecurityGroup = createSecurityGroup("http-security-group", ipAddress, vpc, 80, 443);

        createEc2Instance(sshSecurityGroup, httpSecurityGroup, vpc);
    }

    private SecurityGroup createSecurityGroup(String name, String ipAddress, IVpc vpc, int... ports) {

        var sg = SecurityGroup.Builder.create(this, name)
                .securityGroupName(name)
                .allowAllOutbound(false)
                .vpc(vpc)
                .build();

        for (var port : ports) {
            sg.addIngressRule(Peer.ipv4(ipAddress), Port.tcp(port), "inbound access from my ip");
            sg.addEgressRule(Peer.ipv4(ipAddress), Port.tcp(port), "outbound access to my ip");
        }

        return sg;
    }

    private void createEc2Instance(SecurityGroup sshSecurityGroup, SecurityGroup httpSecurityGroup, IVpc vpc) {

        var keyPair = CfnKeyPair.Builder.create(this, "simple-ec2-instance-key-pair")
                .keyName("simple-ec2-instance-key-pair")
                .keyType("ed25519")
                .keyFormat("pem")
                .build();

        var volume = Volume.Builder.create(this, "simple-ec2-instance-volume")
                .volumeName("simple-ec2-instance-volume")
                .availabilityZone("eu-west-1a")
                .size(Size.gibibytes(8))
                .volumeType(EbsDeviceVolumeType.GP3)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        var userData = UserData.forLinux();
        userData.addCommands("yum update -y",
                "yum install -y httpd",
                "systemctl start httpd",
                "systemctl enable httpd",
                "echo \"Hello World from $(hostname -f)\" > /var/www/html/index.html");

        var instance = Instance.Builder.create(this, "simpleEc2Instance")
                .instanceName("simple-ec2-instance")
                .instanceType(InstanceType.of(InstanceClass.T2, InstanceSize.MICRO))
                .machineImage(MachineImage.latestAmazonLinux2023(AmazonLinux2023ImageSsmParameterProps.builder()
                        .cpuType(AmazonLinuxCpuType.X86_64)
                        .kernel(AmazonLinux2023Kernel.KERNEL_6_1)
                        .build()))
                .keyName(keyPair.getKeyName())
                .securityGroup(httpSecurityGroup)
                .vpc(vpc)
                .availabilityZone("eu-west-1a")
                .userData(userData)
                .build();

        instance.addSecurityGroup(sshSecurityGroup);

        volume.grantAttachVolume(instance);
    }
}
