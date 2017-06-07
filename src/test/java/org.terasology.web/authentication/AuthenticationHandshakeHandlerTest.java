/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.web.authentication;

import org.junit.Test;
import org.terasology.identity.CertificateGenerator;
import org.terasology.identity.CertificatePair;
import org.terasology.identity.IdentityConstants;
import org.terasology.identity.PublicIdentityCertificate;

import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class AuthenticationHandshakeHandlerTest {

    private BigInteger randomBigInteger(Random random) {
        byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
        return new BigInteger(randomBytes);
    }

    private PublicIdentityCertificate randomPublicCert(int seed) {
        Random r = new Random(seed);
        byte[] uuidBytes = new byte[16];
        r.nextBytes(uuidBytes);
        String id = UUID.nameUUIDFromBytes(uuidBytes).toString();
        return new PublicIdentityCertificate(id, randomBigInteger(r), randomBigInteger(r), randomBigInteger(r));
    }

    @Test(expected = AuthenticationFailedException.class)
    public void testInvalidCertificate() throws AuthenticationFailedException {
        CertificateGenerator gen = new CertificateGenerator();
        CertificatePair server = gen.generateSelfSigned();

        AuthenticationHandshakeHandler handshake = new AuthenticationHandshakeHandler(server.getPublicCert());
        handshake.initServerHello();
        HandshakeHello clientHello = new HandshakeHello(new byte[4], randomPublicCert(1), 0);
        handshake.authenticate(clientHello, null);
    }

    @Test
    public void testOk() throws AuthenticationFailedException {
        CertificateGenerator gen = new CertificateGenerator();
        CertificatePair server = gen.generateSelfSigned();
        CertificatePair client = gen.generate(server.getPrivateCert()); //a valid certificate pair signed by the server

        AuthenticationHandshakeHandler handshake = new AuthenticationHandshakeHandler(server.getPublicCert());
        HandshakeHello serverHello = handshake.initServerHello();
        assertTrue(serverHello.getCertificate().verifySelfSigned());
        byte[] clientRandom = new byte[IdentityConstants.SERVER_CLIENT_RANDOM_LENGTH];
        HandshakeHello clientHello = new HandshakeHello(clientRandom, client.getPublicCert(), System.currentTimeMillis());
        byte[] dataToSign = HandshakeHello.concat(serverHello, clientHello);
        byte[] signature = client.getPrivateCert().sign(dataToSign);
        handshake.authenticate(clientHello, signature);
    }
}
