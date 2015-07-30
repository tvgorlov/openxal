package xal.plugins.essrbac;

import se.esss.ics.rbac.access.Credentials;
import se.esss.ics.rbac.access.SecurityCallbackAdapter;
import se.esss.ics.rbac.access.SecurityFacade;
import se.esss.ics.rbac.access.SecurityFacadeException;
import xal.rbac.AccessDeniedException;
import xal.rbac.RBACException;
import xal.rbac.RBACLogin;
import xal.rbac.RBACSubject;

/**
 * EssRbacLogin extends {@link RBACLogin}.
 * 
 * Basically just a wrapper for {@link SecurityFacade} to authenticate.
 * 
 * @version 0.1 27 Jul 2015
 * @author Blaž Kranjc <blaz.kranjc@cosylab.com>
 */
public class EssRbacLogin extends RBACLogin {

    @Override
    public String[] getRolesForUser(final String username) throws RBACException {// TODO test
        try {
            return SecurityFacade.getDefaultInstance().getRolesForUser(username);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RBACException("Couldn't get roles for user: " + username + "\n\r\t");
        }
    }

    @Override
    public RBACSubject authenticate(final String username, final char[] password) throws AccessDeniedException,
            RBACException {
        return authenticate(username, password, null, null);
    }

    @Override
    public RBACSubject authenticate(final String username, final char[] password, final String preferredRole)
            throws AccessDeniedException, RBACException {
        return authenticate(username, password, preferredRole, null);
    }

    @Override
    public RBACSubject authenticate(final String username, final char[] password, final String preferredRole,
            final String ip) throws AccessDeniedException, RBACException {

        SecurityFacade.getDefaultInstance().setDefaultSecurityCallback(new SecurityCallbackAdapter() {
            //We don't need anything else but getCredentials method.
            @Override
            public Credentials getCredentials() {
                return new Credentials(username, password, preferredRole, ip);
            }
        });

        try {
            return new EssRbacSubject(SecurityFacade.getDefaultInstance().authenticate());

        } catch (SecurityFacadeException e) {
            System.err.println(e.getMessage());
            //e.printStackTrace();
            throw new AccessDeniedException("Unable to authenticate.");
        } catch (Exception e){
            System.err.println(e.getMessage());
            throw new RBACException("Error while trying to authenticate.");
        }
    }
}
