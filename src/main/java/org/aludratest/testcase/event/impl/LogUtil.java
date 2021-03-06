/*
 * Copyright (C) 2010-2014 Hamburg Sud and the contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aludratest.testcase.event.impl;

import java.util.Collection;

import org.aludratest.exception.AludraTestException;
import org.aludratest.scheduler.RunnerListenerRegistry;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.service.Action;
import org.aludratest.testcase.AludraTestContext;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.util.ExceptionUtil;
import org.aludratest.util.FlowController;

/** Utility class for functional logging.
 * @author Volker Bergmann */
public class LogUtil {

    private LogUtil() {
        // private constructor for preventing instantiation of this utility class
    }

    public static void logErrorAsNewGroup(RunnerListenerRegistry listenerRegistry, RunnerLeaf leaf, Throwable t, boolean ignore) { // NOSONAR
        listenerRegistry.fireNewTestStepGroup(leaf, "Error");
        TestStepInfoBean info = new TestStepInfoBean();
        info.setCommand("Error report");
        info.setError(ExceptionUtil.unwrapInvocationTargetException(t));
        info.setErrorMessage(info.getError().getMessage());

        if (info.getError() instanceof AludraTestException) {
            info.setTestStatus(((AludraTestException) info.getError()).getTestStatus());
        }
        else {
            info.setTestStatus(TestStatus.INCONCLUSIVE);
        }

        if (ignore) {
            info.setTestStatus(TestStatus.IGNORED);
        }

        listenerRegistry.fireNewTestStep(leaf, info);
    }

    public static void logErrorAsNewGroup(AludraTestContext testContext, String errorMessage, Throwable t) {
        testContext.newTestStepGroup("Error");
        TestStepInfoBean info = new TestStepInfoBean();
        info.setCommand("Error report");
        info.setError(ExceptionUtil.unwrapInvocationTargetException(t));
        info.setErrorMessage(errorMessage);

        if (info.getError() instanceof AludraTestException) {
            info.setTestStatus(((AludraTestException) info.getError()).getTestStatus());
        }
        else {
            info.setTestStatus(TestStatus.INCONCLUSIVE);
        }

        if (FlowController.getInstance().isStopped(testContext)) {
            info.setTestStatus(TestStatus.IGNORED);
        }

        testContext.fireTestStep(info);
    }

    public static void logErrorAsNewGroup(AludraTestContext testContext, String errorMessage, TestStatus status) {
        testContext.newTestStepGroup("Error");
        TestStepInfoBean info = new TestStepInfoBean();
        info.setCommand("Error report");
        info.setErrorMessage(errorMessage);

        info.setTestStatus(status);

        testContext.fireTestStep(info);
    }

    public static void attachDebugAttachments(Action action, TestStepInfoBean testStep) {
        attach(action.createDebugAttachments(), testStep);
    }

    public static void attach(Collection<Attachment> attachments, TestStepInfoBean testStep) {
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                testStep.addAttachment(attachment);
            }
        }
    }

}
