/*
 * Copyright (c) 2014. Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software is licensed under the Apache Licence 2.0. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the terms of the License for more details.
 * SPDX-License-Identifier: Artistic-2.0
 */
package com.stratio.qa.aspects;

import com.stratio.qa.exceptions.NonReplaceableException;
import com.stratio.qa.utils.ThreadProperty;
import org.aspectj.lang.ProceedingJoinPoint;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class ReplacementAspectTest {

    @Test
    public void replaceEmptyPlaceholdersTest() throws NonReplaceableException {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ReplacementAspect repAspect = new ReplacementAspect();
        ProceedingJoinPoint pjp = null;
        assertThat(repAspect.replaceEnvironmentPlaceholders("", pjp)).as("Replacing an empty placeholded string should not modify it").isEqualTo("");
    }

    @Test
    public void replaceSinglePlaceholdersTest() throws NonReplaceableException {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ReplacementAspect repAspect = new ReplacementAspect();
        ProceedingJoinPoint pjp = null;
        System.setProperty("STRATIOBDD_ENV1", "33");
        System.setProperty("STRATIOBDD_ENV2", "aa");

        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}", pjp))
                .as("Unexpected replacement").isEqualTo("33");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}${STRATIOBDD_ENV2}", pjp))
                .as("Unexpected replacement").isEqualTo("33aa");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}:${STRATIOBDD_ENV2}", pjp))
                .as("Unexpected replacement").isEqualTo("33:aa");
        assertThat(repAspect.replaceEnvironmentPlaceholders("|${STRATIOBDD_ENV1}|:|${STRATIOBDD_ENV2}|", pjp))
                .as("Unexpected replacement").isEqualTo("|33|:|aa|");
    }

    @Test
    public void replaceSinglePlaceholderCaseTest() throws NonReplaceableException {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ReplacementAspect repAspect = new ReplacementAspect();
        ProceedingJoinPoint pjp = null;
        System.setProperty("STRATIOBDD_ENV1", "33");
        System.setProperty("STRATIOBDD_ENV2", "aA");

        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1.toUpper}", pjp)).as("Unexpected replacement").isEqualTo("33");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1.toLower}", pjp)).as("Unexpected replacement").isEqualTo("33");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV2.toUpper}", pjp)).as("Unexpected replacement").isEqualTo("AA");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV2.toLower}", pjp)).as("Unexpected replacement").isEqualTo("aa");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}${STRATIOBDD_ENV2.toLower}", pjp)).as("Unexpected replacement").isEqualTo("33aa");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}:${STRATIOBDD_ENV2.toUpper}", pjp)).as("Unexpected replacement").isEqualTo("33:AA");
        assertThat(repAspect.replaceEnvironmentPlaceholders("|${STRATIOBDD_ENV2}.toUpper", pjp)).as("Unexpected replacement").isEqualTo("|aA.toUpper");
    }

    @Test
    public void replaceElementPlaceholderCaseTest() throws NonReplaceableException {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ReplacementAspect repAspect = new ReplacementAspect();
        ProceedingJoinPoint pjp = null;
        System.setProperty("STRATIOBDD_ENV4", "33");
        System.setProperty("STRATIOBDD_ENV5", "aA");

        assertThat(repAspect.replacedElement("${STRATIOBDD_ENV4}", pjp)).isEqualTo("33");
        assertThat(repAspect.replacedElement("${STRATIOBDD_ENV5.toLower}", pjp)).isEqualTo("aa");
        assertThat(repAspect.replacedElement("${STRATIOBDD_ENV5.toUpper}", pjp)).isEqualTo("AA");
        assertThat(repAspect.replacedElement("${STRATIOBDD_ENV5}", pjp)).isEqualTo("aA");
        assertThat(repAspect.replacedElement("${STRATIOBDD_ENV4}${STRATIOBDD_ENV5}", pjp)).isEqualTo("33aA");
        assertThat(repAspect.replacedElement("${STRATIOBDD_ENV4}:${STRATIOBDD_ENV5}", pjp)).isEqualTo("33:aA");
    }
    @Test
    public void replaceReflectionPlaceholderCaseTest() throws NonReplaceableException {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ReplacementAspect repAspect = new ReplacementAspect();
        ProceedingJoinPoint pjp = null;

        assertThatExceptionOfType(Exception.class).isThrownBy(() -> repAspect.replaceReflectionPlaceholders("!{NO_VAL}", pjp));
    }

    @Test
    public void replaceCodePlaceholderCaseTest() throws NonReplaceableException {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ReplacementAspect repAspect = new ReplacementAspect();
        ProceedingJoinPoint pjp = null;

        assertThat(repAspect.replaceCodePlaceholders("@{schemas/simple1.json}", pjp)).isEqualTo("");
        assertThat(repAspect.replaceCodePlaceholders("@{JSON.schemas/simple1.json}", pjp)).isEqualTo("{\"a\":true}");
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> repAspect.replaceCodePlaceholders("@{IP.10.10.10.10}", pjp));
    }

    @Test
    public void replaceMixedPlaceholdersTest() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ThreadProperty.set("STRATIOBDD_LOCAL1", "LOCAL");
        ProceedingJoinPoint pjp = null;
        ReplacementAspect repAspect = new ReplacementAspect();
        System.setProperty("STRATIOBDD_ENV2", "aa");

        assertThat(repAspect.replaceReflectionPlaceholders(repAspect.replaceEnvironmentPlaceholders("!{STRATIOBDD_LOCAL1}:${STRATIOBDD_ENV2}", pjp), pjp))
                .as("Unexpected replacement").isEqualTo("LOCAL:aa");
        assertThat(repAspect.replaceReflectionPlaceholders(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV2}:!{STRATIOBDD_LOCAL1}", pjp), pjp))
                .as("Unexpected replacement").isEqualTo("aa:LOCAL");
    }
}