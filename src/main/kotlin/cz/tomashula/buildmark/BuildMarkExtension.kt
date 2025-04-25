package cz.tomashula.buildmark

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

interface BuildMarkExtension
{
    val targetPackage: Property<String>
    val targetObjectName: Property<String>
    val kotlinSourceSets: ListProperty<KotlinSourceSet>
    val options: MapProperty<String, Any>
}