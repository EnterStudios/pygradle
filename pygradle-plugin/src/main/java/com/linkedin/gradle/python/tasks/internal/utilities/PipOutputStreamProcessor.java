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

package com.linkedin.gradle.python.tasks.internal.utilities;

import com.linkedin.gradle.python.tasks.utilities.DefaultOutputStreamProcessor;
import com.linkedin.gradle.python.utils.OutputUtilities;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PipOutputStreamProcessor extends DefaultOutputStreamProcessor {

    private static final Logger logger = Logging.getLogger(PipOutputStreamProcessor.class);
    private static final Pattern INSTALLING_PACKAGE_PATTERN = Pattern.compile("Installing collected packages: (.*)");
    private static final Pattern INSTALLED_PACKAGE_PATTERN = Pattern.compile("Successfully installed (.*)");

    private static final String GOOD_INSTALL_MESSAGE = "[GOOD]";

    private final Set<String> packages = new HashSet<String>();

    public void processLine(String line) {
        Matcher installingPackageMatcher = INSTALLING_PACKAGE_PATTERN.matcher(line);
        if (installingPackageMatcher.find()) {
            logger.info("Installing {}", installingPackageMatcher.group(1));
            packages.add(installingPackageMatcher.group(1));
        }

        Matcher installedPackageMatcher = INSTALLED_PACKAGE_PATTERN.matcher(line);
        if (installedPackageMatcher.find()) {
            logger.lifecycle(OutputUtilities.writePaddedString(installedPackageMatcher.group(1), GOOD_INSTALL_MESSAGE));
            logger.info("Successfully installed {}", installedPackageMatcher.group(1));
        }
    }

    public Set<String> getPackages() {
        return Collections.unmodifiableSet(packages);
    }
}
