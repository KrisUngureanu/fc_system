package kz.tamur.util.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class SetCharacterEncodingFilter implements Filter {

    /**
     * The default character encoding to set for requests that pass through
     * this filter.
     */
    private String encoding = "UTF-8";
    public void setEncoding(String encoding) { this.encoding = encoding; }
    public String getEncoding() { return encoding; }


    /**
     * Should a character encoding specified by the client be ignored?
     */
    private boolean ignore = false;
    public void setIgnore(boolean ignore) { this.ignore = ignore; }
    public boolean isIgnore() { return ignore; }


    // --------------------------------------------------------- Public Methods


    /**
     * Select and set (if specified) the character encoding to be used to
     * interpret request parameters for this request.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
        throws IOException, ServletException {

        // Conditionally select and set the character encoding to be used
        if (ignore || (request.getCharacterEncoding() == null)) {
            if (this.encoding != null)
                request.setCharacterEncoding(this.encoding);
        }

        // Pass control on to the next filter
        chain.doFilter(request, response);
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String temp = filterConfig.getInitParameter("encoding");
        if (temp != null)
        	this.encoding = temp;
        
        temp = filterConfig.getInitParameter("ignore");
        if (temp != null)
        	this.ignore = Boolean.getBoolean(temp);
    }

    @Override
    public void destroy() {
        // NOOP
    }
}
