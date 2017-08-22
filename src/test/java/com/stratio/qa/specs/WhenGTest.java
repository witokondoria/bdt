/*
 * Copyright (c) 2014. Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software is licensed under the Apache Licence 2.0. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the terms of the License for more details.
 * SPDX-License-Identifier: Artistic-2.0
 */
package com.stratio.qa.specs;

import com.stratio.qa.utils.ThreadProperty;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import cucumber.api.DataTable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Created by carlosgarcia on 29/07/16.
 */
public class WhenGTest {

    @Test
    public void testSortJSONElementsAscending() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());

        String baseData = "indicesJSON.conf";
        String ascendingFile = "indicesJSONAscending.conf";
        String envVar = "envVar";

        String jsonString = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));
        String jsonStringAscending = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(ascendingFile).getFile())));

        CommonG commong = new CommonG();
        WhenGSpec wheng = new WhenGSpec(commong);

        ThreadProperty.set(envVar, jsonString);

        try {
            wheng.sortElements(envVar, "alphabetical", "ascending");
        } catch (Exception e) {
            fail("Error parsing JSON String");
        }

        String value = ThreadProperty.get(envVar);

        assertThat(value).as("Not correctly ordered").isEqualTo(jsonStringAscending);
    }

    @Test
    public void testSortJSONElementsDescending() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());

        String baseData = "indicesJSON.conf";
        String descendingFile = "indicesJSONDescending.conf";
        String envVar = "envVar";

        String jsonString = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));
        String jsonStringDescending = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(descendingFile).getFile())));

        CommonG commong = new CommonG();
        WhenGSpec wheng = new WhenGSpec(commong);

        ThreadProperty.set(envVar, jsonString);

        try {
            wheng.sortElements(envVar, "alphabetical", "descending");
        } catch (Exception e) {
            fail("Error parsing JSON String");
        }

        String value = ThreadProperty.get(envVar);

        assertThat(value).as("Not correctly ordered").isEqualTo(jsonStringDescending);
    }

    @Test
    public void testReadFileToVariableJSON() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());

        String baseData = "schemas/testCreateFile.json";
        String type = "json";
        String envVar = "myjson";
        List<List<String>> rawData = Arrays.asList(Arrays.asList("key1", "UPDATE", "new_value", "n/a"), Arrays.asList("key2", "ADDTO", "[\"new_value\"]", "array"));
        DataTable modifications = DataTable.create(rawData);

        CommonG commong = new CommonG();
        WhenGSpec wheng = new WhenGSpec(commong);

        wheng.readFileToVariable(baseData, type, envVar, modifications);

        String envVarResult = ThreadProperty.get(envVar);
        String expectedResult = "{\"key1\":\"new_value\",\"key2\":[[\"new_value\"]],\"key3\":{\"key3_2\":\"value3_2\",\"key3_1\":\"value3_1\"}}";

        assertThat(envVarResult).as("Not as expected").isEqualTo(expectedResult);
    }

    @Test
    public void testReadFileToVariableString() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());

        String baseData = "schemas/krb5.conf";
        String type = "string";
        String envVar = "mystring";
        List<List<String>> rawData = Arrays.asList(Arrays.asList("foo", "REPLACE", "bar", "n/a"));
        DataTable modifications = DataTable.create(rawData);

        CommonG commong = new CommonG();
        WhenGSpec wheng = new WhenGSpec(commong);

        wheng.readFileToVariable(baseData, type, envVar, modifications);

        String envVarResult = ThreadProperty.get(envVar);
        String expectedResult = "bar = bar";

        assertThat(envVarResult).as("Not as expected").isEqualTo(expectedResult);
    }

}
