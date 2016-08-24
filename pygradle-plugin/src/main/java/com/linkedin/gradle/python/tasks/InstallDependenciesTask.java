/*
 * Copyright 2016 LinkedIn Corp.
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

package com.linkedin.gradle.python.tasks;

import com.linkedin.gradle.python.internal.toolchain.PythonExecutable;
import com.linkedin.gradle.python.tasks.internal.utilities.PipDependencyInstallAction;
import com.linkedin.gradle.python.tasks.internal.utilities.PipInstallHelper;
import com.linkedin.gradle.python.tasks.internal.utilities.TaskUtils;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectories;
import org.gradle.api.tasks.TaskAction;


public class InstallDependenciesTask extends BasePythonTask {

    private Configuration dependencyConfiguration;

    @TaskAction
    public void installDependencies() {
        PythonExecutable pythonExecutable = getPythonEnvironment().getVirtualEnvPythonExecutable();
        PipInstallHelper pipInstallHelper = new PipInstallHelper(pythonExecutable, new PipDependencyInstallAction(getVenvDir()));
        preformFullInstall(pipInstallHelper);
    }

    private void preformFullInstall(PipInstallHelper pipInstallHelper) {
        for (final File dependency : getDependencyConfiguration()) {
            getLogger().info("Installing {}", dependency.getAbsoluteFile());
            pipInstallHelper.install(dependency);
        }
    }

    @OutputDirectories
    public Set<File> getDependencies() {
        Set<File> insalledSitePackages = getSitePackageFolderSet(dependencyConfiguration.getDependencies());
        getLogger().info("Packages dir: {}", insalledSitePackages);
        return insalledSitePackages;
    }

    private Set<File> getSitePackageFolderSet(DependencySet dependencies) {
        HashSet<File> sitePackages = new HashSet<File>();
        for (Dependency dependency : dependencies) {
            sitePackages.add(new File(TaskUtils.sitePackage(getVenvDir(), pythonEnvironment.getVersion()), dependency.getName()));
        }
        return sitePackages;
    }

    @InputFiles
    public Configuration getDependencyConfiguration() {
        getLogger().info("Input dir: {}", dependencyConfiguration);
        return dependencyConfiguration;
    }

    public void setDependencyConfiguration(Configuration configuration) {
        this.dependencyConfiguration = configuration;
    }
}
