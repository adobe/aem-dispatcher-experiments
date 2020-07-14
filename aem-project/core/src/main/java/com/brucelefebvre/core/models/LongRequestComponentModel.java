/*
Copyright 2020 Adobe. All rights reserved.
This file is licensed to you under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License. You may obtain a copy
of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under
the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
OF ANY KIND, either express or implied. See the License for the specific language
governing permissions and limitations under the License.
*/
package com.brucelefebvre.core.models;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Model(adaptables = Resource.class)
public class LongRequestComponentModel {

    @Inject
    private String endpoint;

    public String getData() throws InterruptedException, IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        String result = "";
        
        try
        {
            HttpGet getRequest = new HttpGet(this.endpoint);
            getRequest.addHeader("accept", "application/json");

            HttpResponse response = client.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String line;
            while ((line = br.readLine()) != null) {
                //System.out.println(output);
                result = result + line;
            }
        }
        catch (Exception e)
        {
            result = "UHO! Something went wrong. Is the node-server running?";
            e.printStackTrace() ;
        }
        finally {
            client.close();
        }

        return result;
    }

}
