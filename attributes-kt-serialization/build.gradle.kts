plugins {
    id("space.kscience.gradle.mpp")
    `maven-publish`
}

description = "A generic serializer for attributes"

kscience {
    jvm()
    js()
    native()
    wasmJs()

    useSerialization{
        json()
    }

    commonMain {
        api(projects.attributesKt)
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
}