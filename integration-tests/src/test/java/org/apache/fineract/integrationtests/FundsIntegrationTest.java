/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.integrationtests;

import static org.apache.fineract.client.feign.util.FeignCalls.fail;
import static org.apache.fineract.client.feign.util.FeignCalls.ok;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.UUID;
import org.apache.fineract.client.feign.FineractFeignClient;
import org.apache.fineract.client.feign.util.CallFailedRuntimeException;
import org.apache.fineract.client.models.FundData;
import org.apache.fineract.client.models.FundRequest;
import org.apache.fineract.client.models.PostFundsResponse;
import org.apache.fineract.client.models.PutFundsFundIdResponse;
import org.apache.fineract.integrationtests.common.FineractFeignClientHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Funds Integration Test for checking Funds Application.
 */
public class FundsIntegrationTest {

    private final FineractFeignClient fineractClient = FineractFeignClientHelper.getFineractFeignClient();

    @Test
    public void testCreateFund() {
        FundRequest request = new FundRequest().name(Utils.uniqueRandomStringGenerator("", 10)).externalId(UUID.randomUUID().toString());

        PostFundsResponse response = ok(() -> fineractClient.funds().createFund(request));
        assertNotNull(response.getResourceId());
    }

    @Test
    public void testCreateFundWithEmptyName() {
        FundRequest request = new FundRequest().externalId(UUID.randomUUID().toString());

        CallFailedRuntimeException exception = fail(() -> fineractClient.funds().createFund(request));
        assertEquals(400, exception.getStatus());
    }

    @Test
    public void testCreateFundWithEmptyExternalId() {
        FundRequest request = new FundRequest().name(Utils.uniqueRandomStringGenerator("", 10));

        PostFundsResponse response = ok(() -> fineractClient.funds().createFund(request));
        assertNotNull(response.getResourceId());
    }

    @Test
    public void testCreateFundWithDuplicateName() {
        String name = Utils.uniqueRandomStringGenerator("", 10);
        FundRequest request = new FundRequest().name(name).externalId(UUID.randomUUID().toString());

        PostFundsResponse response = ok(() -> fineractClient.funds().createFund(request));
        assertNotNull(response.getResourceId());

        FundRequest duplicateRequest = new FundRequest().name(name).externalId(UUID.randomUUID().toString());

        CallFailedRuntimeException exception = fail(() -> fineractClient.funds().createFund(duplicateRequest));
        assertEquals(403, exception.getStatus());
    }

    @Test
    public void testCreateFundWithDuplicateExternalId() {
        String externalId = UUID.randomUUID().toString();
        FundRequest request = new FundRequest().name(Utils.uniqueRandomStringGenerator("", 10)).externalId(externalId);

        PostFundsResponse response = ok(() -> fineractClient.funds().createFund(request));
        assertNotNull(response.getResourceId());

        FundRequest duplicateRequest = new FundRequest().name(Utils.uniqueRandomStringGenerator("", 10)).externalId(externalId);

        CallFailedRuntimeException exception = fail(() -> fineractClient.funds().createFund(duplicateRequest));
        assertEquals(403, exception.getStatus());
    }

    @Test
    public void testCreateFundWithInvalidName() {
        FundRequest request = new FundRequest().name(Utils.randomStringGenerator("", 120)).externalId(UUID.randomUUID().toString());

        CallFailedRuntimeException exception = fail(() -> fineractClient.funds().createFund(request));
        assertEquals(400, exception.getStatus());
    }

    @Test
    public void testCreateFundWithInvalidExternalId() {
        FundRequest request = new FundRequest().name(Utils.uniqueRandomStringGenerator("", 10))
                .externalId(Utils.randomStringGenerator("fund-", 120));

        CallFailedRuntimeException exception = fail(() -> fineractClient.funds().createFund(request));
        assertEquals(400, exception.getStatus());
    }

    @Test
    public void testRetrieveFund() {
        String name = Utils.uniqueRandomStringGenerator("", 10);
        FundRequest request = new FundRequest().name(name).externalId(UUID.randomUUID().toString());

        PostFundsResponse createResponse = ok(() -> fineractClient.funds().createFund(request));
        assertNotNull(createResponse.getResourceId());

        FundData fund = ok(() -> fineractClient.funds().retrieveFund(createResponse.getResourceId()));
        assertEquals(name, fund.getName());
    }

    @Test
    public void testRetrieveAllFunds() {
        String name = Utils.uniqueRandomStringGenerator("", 10);
        FundRequest request = new FundRequest().name(name).externalId(UUID.randomUUID().toString());

        PostFundsResponse createResponse = ok(() -> fineractClient.funds().createFund(request));
        assertNotNull(createResponse.getResourceId());

        List<FundData> funds = ok(() -> fineractClient.funds().retrieveFunds());

        Assertions.assertNotNull(funds);
        assertThat(funds.size(), greaterThanOrEqualTo(1));
        Assertions.assertTrue(funds.stream().anyMatch(f -> name.equals(f.getName())));
    }

    @Test
    public void testRetrieveUnknownFund() {
        CallFailedRuntimeException exception = fail(() -> fineractClient.funds().retrieveFund(Long.MAX_VALUE));
        assertEquals(404, exception.getStatus());
    }

    @Test
    public void testUpdateFund() {
        FundRequest createRequest = new FundRequest().name(Utils.uniqueRandomStringGenerator("", 10))
                .externalId(UUID.randomUUID().toString());

        PostFundsResponse createResponse = ok(() -> fineractClient.funds().createFund(createRequest));
        assertNotNull(createResponse.getResourceId());

        String newName = Utils.uniqueRandomStringGenerator("", 10);
        String newExternalId = UUID.randomUUID().toString();
        FundRequest updateRequest = new FundRequest().name(newName).externalId(newExternalId);

        PutFundsFundIdResponse updateResponse = ok(() -> fineractClient.funds().updateFund(createResponse.getResourceId(), updateRequest));
        assertNotNull(updateResponse);

        FundData fund = ok(() -> fineractClient.funds().retrieveFund(createResponse.getResourceId()));
        assertEquals(newName, fund.getName());
        assertEquals(newExternalId, fund.getExternalId());
    }

    @Test
    public void testUpdateUnknownFund() {
        FundRequest updateRequest = new FundRequest().name(Utils.uniqueRandomStringGenerator("", 10))
                .externalId(UUID.randomUUID().toString());

        CallFailedRuntimeException exception = fail(() -> fineractClient.funds().updateFund(Long.MAX_VALUE, updateRequest));
        assertEquals(404, exception.getStatus());
    }

    @Test
    public void testUpdateFundWithInvalidNewName() {
        FundRequest createRequest = new FundRequest().name(Utils.uniqueRandomStringGenerator("", 10))
                .externalId(UUID.randomUUID().toString());

        PostFundsResponse createResponse = ok(() -> fineractClient.funds().createFund(createRequest));
        assertNotNull(createResponse.getResourceId());

        FundRequest updateRequest = new FundRequest().name(Utils.randomStringGenerator("", 120)).externalId(UUID.randomUUID().toString());

        CallFailedRuntimeException exception = fail(() -> fineractClient.funds().updateFund(createResponse.getResourceId(), updateRequest));
        assertEquals(400, exception.getStatus());
    }

    @Test
    public void testUpdateFundWithNewExternalId() {
        FundRequest createRequest = new FundRequest().name(Utils.uniqueRandomStringGenerator("", 10))
                .externalId(UUID.randomUUID().toString());

        PostFundsResponse createResponse = ok(() -> fineractClient.funds().createFund(createRequest));
        assertNotNull(createResponse.getResourceId());

        String newExternalId = UUID.randomUUID().toString();
        FundRequest updateRequest = new FundRequest().externalId(newExternalId);

        ok(() -> fineractClient.funds().updateFund(createResponse.getResourceId(), updateRequest));

        FundData fund = ok(() -> fineractClient.funds().retrieveFund(createResponse.getResourceId()));
        assertEquals(newExternalId, fund.getExternalId());
    }

    @Test
    public void testUpdateFundWithInvalidNewExternalId() {
        FundRequest createRequest = new FundRequest().name(Utils.uniqueRandomStringGenerator("", 10))
                .externalId(UUID.randomUUID().toString());

        PostFundsResponse createResponse = ok(() -> fineractClient.funds().createFund(createRequest));
        assertNotNull(createResponse.getResourceId());

        FundRequest updateRequest = new FundRequest().name(Utils.uniqueRandomStringGenerator("", 10))
                .externalId(Utils.randomStringGenerator("fund-", 120));

        CallFailedRuntimeException exception = fail(() -> fineractClient.funds().updateFund(createResponse.getResourceId(), updateRequest));
        assertEquals(400, exception.getStatus());
    }

    @Test
    public void testUpdateFundWithNewName() {
        FundRequest createRequest = new FundRequest().name(Utils.uniqueRandomStringGenerator("", 10))
                .externalId(UUID.randomUUID().toString());

        PostFundsResponse createResponse = ok(() -> fineractClient.funds().createFund(createRequest));
        assertNotNull(createResponse.getResourceId());

        String newName = Utils.uniqueRandomStringGenerator("", 10);
        FundRequest updateRequest = new FundRequest().name(newName);

        ok(() -> fineractClient.funds().updateFund(createResponse.getResourceId(), updateRequest));

        FundData fund = ok(() -> fineractClient.funds().retrieveFund(createResponse.getResourceId()));
        assertEquals(newName, fund.getName());
    }

    @Test
    public void testUpdateFundWithEmptyParams() {
        String originalName = Utils.uniqueRandomStringGenerator("", 10);
        String originalExternalId = UUID.randomUUID().toString();
        FundRequest createRequest = new FundRequest().name(originalName).externalId(originalExternalId);

        PostFundsResponse createResponse = ok(() -> fineractClient.funds().createFund(createRequest));
        assertNotNull(createResponse.getResourceId());

        FundRequest updateRequest = new FundRequest();
        ok(() -> fineractClient.funds().updateFund(createResponse.getResourceId(), updateRequest));

        // assert that there was no change in the name and external ID of the fund
        FundData fund = ok(() -> fineractClient.funds().retrieveFund(createResponse.getResourceId()));
        assertEquals(originalName, fund.getName());
        assertEquals(originalExternalId, fund.getExternalId());
    }
}
