import space.kscience.gradle.useApache2Licence
import space.kscience.gradle.useSPCTeam

plugins {
    id("space.kscience.gradle.project")
    id("space.kscience.gradle.mpp")
    `maven-publish`
}

description = "Type safe object attributes and builders for them"

allprojects {
    group = "space.kscience"
    version = "0.4.0-dev"
}

kscience {
    jvm()
    js()
    native()
    wasmJs()
}

ksciencePublish {
    pom("https://git.sciprog.center/kscience/attributes-kt") {
        useApache2Licence()
        useSPCTeam()
    }
    repository("spc", "https://maven.sciprog.center/kscience")
    central()
}

readme {
    maturity = space.kscience.gradle.Maturity.DEVELOPMENT
    readmeTemplate = file("docs/templates/README-TEMPLATE.md")
}
