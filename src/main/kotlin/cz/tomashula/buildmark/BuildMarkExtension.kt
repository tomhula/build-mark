package cz.tomashula.buildmark

import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

interface BuildMarkExtension
{
    val targetPackage: Property<String>
    val targetObjectName: Property<String>
    val options: MapProperty<String, Any>
}