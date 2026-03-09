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
package org.apache.fineract.integrationtests.common.funds;

import static org.apache.fineract.client.feign.util.FeignCalls.ok;

import java.util.List;
import java.util.UUID;
import org.apache.fineract.client.feign.FineractFeignClient;
import org.apache.fineract.client.models.FundData;
import org.apache.fineract.client.models.FundRequest;
import org.apache.fineract.client.models.PostFundsResponse;
import org.apache.fineract.client.models.PutFundsFundIdResponse;
import org.apache.fineract.integrationtests.common.FineractFeignClientHelper;
import org.apache.fineract.integrationtests.common.Utils;

public final class FundsResourceHandler {

    private FundsResourceHandler() {}

    private static FineractFeignClient feignClient() {
        return FineractFeignClientHelper.getFineractFeignClient();
    }

    public static PostFundsResponse createFund(final FundRequest request) {
        return ok(() -> feignClient().funds().createFund(request));
    }

    public static PostFundsResponse createFundWithRandomData() {
        FundRequest request = new FundRequest().name(Utils.uniqueRandomStringGenerator("Fund_", 10))
                .externalId(UUID.randomUUID().toString());
        return createFund(request);
    }

    public static List<FundData> retrieveAllFunds() {
        return ok(() -> feignClient().funds().retrieveFunds());
    }

    public static FundData retrieveFund(final Long fundId) {
        return ok(() -> feignClient().funds().retrieveFund(fundId));
    }

    public static PutFundsFundIdResponse updateFund(final Long fundId, final FundRequest request) {
        return ok(() -> feignClient().funds().updateFund(fundId, request));
    }

}
