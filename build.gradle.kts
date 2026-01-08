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
    version = "0.4.0"
}

kscience {
    jvm()
    js()
    native()
    wasmJs()
}

kscienceProject {
    pom("https://git.sciprog.center/kscience/attributes-kt") {
        useApache2Licence()
        useSPCTeam()
    }
    publishTo("spc", "https://maven.sciprog.center/kscience")
    publishToCentral()
}

readme {
    maturity = space.kscience.gradle.Maturity.DEVELOPMENT
    readmeTemplate = file("docs/templates/README-TEMPLATE.md")
}
