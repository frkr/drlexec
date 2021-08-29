/*
 * MIT License
 *
 * Copyright (c) 2021 Davi Saranszky Mesquita
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.github.frkr.drlexec.service;

import com.github.frkr.drlexec.bean.request.exemplo.Exemplo;
import com.github.frkr.drlexec.bean.response.Retorno;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "EntryPoint", externalDocs = @ExternalDocumentation(description = "Doc", url = "https://github.com/frkr/drlexec"))
public class EntryPointService {

    private final static KieContainer kc;

    static {
        kc = KieServices.Factory.get().getKieClasspathContainer();
    }

    @Operation(summary = "Executar Regras: Testes")
    @RequestMapping(value = "teste", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Retorno> motor(
            @RequestBody Exemplo json
    ) throws Throwable {
        Retorno retorno = new Retorno();
        retorno.setBom("teste");
        retorno.setObject(json);

        KieSession session = kc.newKieSession();
        long inicio = System.currentTimeMillis();
//        if (droolsTrace) {
//            kieSession.addEventListener(new DebugRuleRuntimeEventListener()); // TODO testar oq sai de diferente no LOG
//        }
        try {
            session.setGlobal("retorno", retorno);
            session.insert(json);
            retorno.setRules(session.fireAllRules());
        } finally {
            retorno.setTimeMillis((System.currentTimeMillis() - inicio));
            try {
                session.dispose();
            } catch (Exception e) {

            }
            //log.debug(json);
        }

        return ResponseEntity.ok(retorno);
    }
}
