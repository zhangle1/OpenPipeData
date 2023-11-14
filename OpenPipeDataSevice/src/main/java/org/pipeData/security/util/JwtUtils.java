package org.pipeData.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.pipeData.core.base.consts.Const;
import org.pipeData.core.common.Application;
import org.pipeData.core.entity.User;
import org.pipeData.security.base.InviteToken;
import org.pipeData.security.base.JwtToken;
import org.pipeData.security.base.PasswordToken;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class JwtUtils {

    private static final String TOKEN_KEY_ORG_ID = "org_id";

    private static final String TOKEN_KEY_USER_ID = "user_id";

    private static final String TOKEN_KEY_INVITER = "inviter";

    private static final String PASSWORD_HASH = "password";

    public static final int VERIFY_CODE_TIMEOUT_MIN = 10 * 60 * 1000;


    public static JwtToken createJwtToken(User user) {
        JwtToken jwtToken = new JwtToken();
        jwtToken.setSubject(user.getUsername());
        jwtToken.setExp(new Date(System.currentTimeMillis() + getSessionTimeout()));
        jwtToken.setPwdHash(user.getPassword().hashCode());
        return jwtToken;
    }

    public static String toJwtString(JwtToken token) {
        HashMap<String, Object> claims = new HashMap<>();
        if (token instanceof PasswordToken) {
            claims.put(PASSWORD_HASH, ((PasswordToken) token).getPassword().hashCode());
        } else {
            claims.put(PASSWORD_HASH, token.getPwdHash());
        }
        byte[] apiKeySecretBytes = Base64.getDecoder().decode(getBase64Security());
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());


        String jwt = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuer("OpenPipe")
                .setAudience("OpenPipeService")
                .setClaims(claims)
                .setSubject(token.getSubject())
                .setExpiration(token.getExp() != null ? token.getExp() : new Date(token.getCreateTime() + getSessionTimeout()))
                .signWith(signingKey)
                .compact();
        return Const.TOKEN_HEADER_PREFIX + jwt;
    }

    public static String toJwtString(InviteToken token) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_KEY_INVITER, token.getInviter());
        claims.put(TOKEN_KEY_USER_ID, token.getUserId());
        claims.put(TOKEN_KEY_ORG_ID, token.getOrgId());

        byte[] apiKeySecretBytes = Base64.getDecoder().decode(getBase64Security());
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        String jwt = Jwts.builder()
                .setClaims(claims)
                .setSubject(token.getSubject())
                .setExpiration(token.getExp() != null ? token.getExp() : new Date(token.getCreateTime() + VERIFY_CODE_TIMEOUT_MIN))
                .signWith(signingKey)
                .compact();
        return Const.TOKEN_HEADER_PREFIX + jwt;
    }

    public static JwtToken toJwtToken(String jwtString) {
        if (jwtString == null || !jwtString.startsWith(Const.TOKEN_HEADER_PREFIX)) {
            return null;
        }
        jwtString = StringUtils.removeStart(jwtString, Const.TOKEN_HEADER_PREFIX);
        Claims claims = getClaims(jwtString);
        JwtToken jwtToken = new JwtToken();
        jwtToken.setSubject(claims.getSubject());
        jwtToken.setPwdHash(claims.get(PASSWORD_HASH, Integer.class));
        jwtToken.setExp(claims.getExpiration());
        return jwtToken;
    }

//    public static PasswordToken toPasswordToken(String jwtString) {
//        try {
//            jwtString = URLDecoder.decode(jwtString, StandardCharsets.UTF_8.displayName());
//        } catch (Exception ignored) {
//        }
//        if (jwtString == null || !jwtString.startsWith(Const.TOKEN_HEADER_PREFIX)) {
//            return null;
//        }
//        jwtString = StringUtils.removeStart(jwtString, Const.TOKEN_HEADER_PREFIX);
//        Claims claims = getClaims(jwtString);
//        PasswordToken passwordToken = new PasswordToken();
//        passwordToken.setSubject(claims.getSubject());
//        passwordToken.setPassword(claims.get(PASSWORD_HASH, String.class));
//        passwordToken.setExp(claims.getExpiration());
//        return passwordToken;
//    }


    public static InviteToken toInviteToken(String token) {
        token = StringUtils.removeStart(token, Const.TOKEN_HEADER_PREFIX);
        Claims claims = getClaims(token);
        InviteToken inviteToken = new InviteToken();
        inviteToken.setInviter(claims.get(TOKEN_KEY_INVITER, String.class));
        inviteToken.setUserId(claims.get(TOKEN_KEY_USER_ID, String.class));
        inviteToken.setOrgId(claims.get(TOKEN_KEY_ORG_ID, String.class));
        return inviteToken;
    }

    public static boolean validTimeout(String jwtToken) {
        return validTimeout(toJwtToken(jwtToken));
    }


    public static boolean validTimeout(JwtToken token) {
        return token.getExp() == null || token.getExp().after(Calendar.getInstance().getTime());
    }

    private static Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(Application.getTokenSecret().getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token.trim())
                .getBody();
    }

    private static long getSessionTimeout() {
        String timeout = Application.getProperty("pipeData.security.token.timeout-min", "30");
        return Long.parseLong(timeout) * 60 * 1000;
    }




    public static String getBase64Security() {
        return Base64.getEncoder().encodeToString("vcGEm1ww4LUiOcy26zFSPTht1SZPso9m".getBytes(StandardCharsets.UTF_8));
    }

}
