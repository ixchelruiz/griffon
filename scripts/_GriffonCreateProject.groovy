/*
 * Copyright 2004-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import griffon.util.GriffonUtil

/**
 * Gant script that handles the creation of Griffon applications
 *
 * @author Graeme Rocher (Grails 0.4)
 */

includeTargets << griffonScript("_GriffonInit")
includeTargets << griffonScript("CreateMvc" )
includeTargets << griffonScript("Package")


griffonAppName = ""
projectType = "app"

target(createApp: "Creates a Griffon application for the given name")  {
    depends(parseArguments, appName)
    metadataFile = new File("${basedir}/application.properties")
    initProject()
    ant.replace(dir:"${basedir}/griffon-app/conf", includes:"**/*.*") {
        replacefilter(token: "@griffon.app.class.name@", value:appClassName )
        replacefilter(token: "@griffon.version@", value: griffonVersion)
        replacefilter(token: "@griffon.project.name@", value: griffonAppName)
        replacefilter(token: "@griffon.project.key@", value: griffonAppName.replaceAll( /\s/, '.' ).toLowerCase())
    }

    // Create a message bundle to get the user started.
    touch(file: metadataFile)

    // Create a message bundle to get the user started.
    ant.touch(file: "${basedir}/griffon-app/i18n/messages.properties")

    createMVC()

	// Set the default version number for the application
    ant.propertyfile(file:metadataFile.absolutePath) {
        entry(key:"app.version", value: griffonAppVersion ?: "0.1")
//        entry(key:"app.servlet.version", value:servletVersion)
    }

    event("StatusFinal", ["Created Griffon Application at $basedir"])
}


target(createPlugin: "The implementation target")  {
    depends(parseArguments, appName)
    metadataFile = new File("${basedir}/application.properties")
    projectType = "plugin"
    initProject(type: "plugin")

    // Rename the plugin descriptor.
    pluginName = GriffonUtil.getNameFromScript(griffonAppName)
    ant.move(
            file: "${basedir}/GriffonPlugin.groovy",
            tofile: "${basedir}/${pluginName}GriffonPlugin.groovy",
            overwrite: true)

    // Insert the name of the plugin into whatever files need it.
    ant.replace(dir:"${basedir}") {
        include(name: "*GriffonPlugin.groovy")
        include(name: "scripts/*")
        replacefilter(token: "@plugin.name@", value: pluginName)
        replacefilter(token: "@griffon.version@", value: griffonVersion)
    }

    event("StatusFinal", [ "Created plugin ${pluginName}" ])
}

target(initProject: "Initialise an application or plugin project") {
    depends(createStructure, updateAppProperties)

    griffonUnpack(dest: basedir, src: "griffon-shared-files.jar")
    griffonUnpack(dest: basedir, src: "griffon-$projectType-files.jar")
    launderIDESupportFiles()
}

target ( appName : "Evaluates the application name") {
    if(!argsMap["params"]) {
		ant.input(message:"Application name not specified. Please enter:",
				  addProperty:"griffon.app.name")
		griffonAppName = ant.antProject.properties."griffon.app.name"
        argsMap["params"] << griffonAppName
	}
	else {
		griffonAppName = argsMap["params"].join(" ")
	}

    if (argsMap["inplace"]) {
        if (!args) {
            println "WARNING!"
        }
    }
    else {
        basedir = "${basedir}/${griffonAppName}"
    }

    if (argsMap["appVersion"]) {
        griffonAppVersion = argsMap["appVersion"]
    }

    appClassName = GriffonUtil.getClassNameRepresentation(griffonAppName)
}
