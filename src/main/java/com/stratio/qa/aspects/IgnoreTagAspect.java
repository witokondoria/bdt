/*
 * Copyright (c) 2014. Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software is licensed under the Apache Licence 2.0. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the terms of the License for more details.
 * SPDX-License-Identifier: Artistic-2.0
 */

package com.stratio.qa.aspects;

import cucumber.runtime.Runtime;
import cucumber.runtime.model.CucumberScenario;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Tag;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.stratio.qa.aspects.IgnoreTagAspect.ignoreReasons.*;

@Aspect
public class IgnoreTagAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @Pointcut("execution (* cucumber.runtime.model.CucumberScenario.run(..)) && "
            + "args (formatter, reporter, runtime)")
    protected void addIgnoreTagPointcutScenario(Formatter formatter, Reporter reporter, Runtime runtime) {
    }

    /**
     * @param pjp ProceedingJoinPoint
     * @param formatter formatter
     * @param reporter reporter
     * @param runtime runtime
     * @throws Throwable exception
     */
    @Around(value = "addIgnoreTagPointcutScenario(formatter, reporter, runtime)")
    public void aroundAddIgnoreTagPointcut(ProceedingJoinPoint pjp, Formatter formatter, Reporter reporter,
                                           Runtime runtime) throws Throwable {

        CucumberScenario scen = (CucumberScenario) pjp.getThis();
        Scenario scenario = (Scenario) scen.getGherkinModel();

        Class<?> sc = scen.getClass();
        Method tt = sc.getSuperclass().getDeclaredMethod("tagsAndInheritedTags");
        tt.setAccessible(true);
        Set<Tag> tags = (Set<Tag>) tt.invoke(scen);

        List<String> tagList = new ArrayList<>();
        String scenarioName = scenario.getName();
        tagList = tags.stream().map(Tag::getName).collect(Collectors.toList());

        ignoreReasons exitReason = manageTags(tagList, scenarioName);
        if (exitReason.equals(NOREASON)) {
            logger.error("Scenario '" + scenario.getName() + "' failed due to wrong use of the @ignore tag. ");
        }

        if ((!(exitReason.equals(NOTIGNORED))) && (!(exitReason.equals(NOREASON)))) {
            runtime.buildBackendWorlds(reporter, tags, scenario.getName());
            formatter.startOfScenarioLifeCycle(scenario);
            formatter.endOfScenarioLifeCycle(scenario);
            runtime.disposeBackendWorlds();
        } else {
            pjp.proceed();
        }
    }

    public ignoreReasons manageTags(List<String> tagList, String scenarioName) {
        ignoreReasons exit = NOTIGNORED;
        if (tagList.contains("@ignore")) {
            exit = ignoreReasons.NOREASON;
            for (String tag: tagList) {
                Pattern pattern = Pattern.compile("@tillfixed\\((.*?)\\)");
                Matcher matcher = pattern.matcher(tag);
                if (matcher.find()) {
                    String ticket = matcher.group(1);
                    logger.warn("Scenario '" + scenarioName + "' ignored because of ticket: " + ticket);
                    exit = ignoreReasons.JIRATICKET;
                }
            }
            if (tagList.contains("@envCondition")) {
                exit = ignoreReasons.ENVCONDITION;
            }
            if (tagList.contains("@unimplemented")) {
                logger.warn("Scenario '" + scenarioName + "' ignored because it is not yet implemented.");
                exit = ignoreReasons.UNIMPLEMENTED;
            }
            if (tagList.contains("@manual")) {
                logger.warn("Scenario '" + scenarioName + "' ignored because it is marked as manual test.");
                exit = ignoreReasons.MANUAL;
            }
            if (tagList.contains("@toocomplex")) {
                logger.warn("Scenario '" + scenarioName + "' ignored because the test is too complex.");
                exit = ignoreReasons.TOOCOMPLEX;
            }
        }
        return exit;
    }

    public enum ignoreReasons { NOTIGNORED, ENVCONDITION, UNIMPLEMENTED, MANUAL, TOOCOMPLEX, JIRATICKET, NOREASON }
}