/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.gradle.tasks

import org.gradle.api.Project

/**
 * @author Andres Almiray
 */
class AggregateCoberturaExtension {
    static final String EXTENSION_NAME = 'aggregateCobertura'
    static final String COBERTURA_NAME = 'cobertura'

    File coverageOutputDatafile

    File coverageReportDir

    AggregateCoberturaExtension(Project project) {
        project.logger.info "Creating aggregateCobertura extension for project ${project.name}"
        coverageOutputDatafile = new File("${project.buildDir.path}/cobertura", 'cobertura.ser')
        coverageReportDir = new File("${project.reporting.baseDir.path}/cobertura")
    }
}
