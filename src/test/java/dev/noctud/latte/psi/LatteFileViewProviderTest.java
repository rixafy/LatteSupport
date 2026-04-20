package dev.noctud.latte.psi;

import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightVirtualFile;
import dev.noctud.latte.BasePsiParsingTestCase;
import dev.noctud.latte.LatteLanguage;

public class LatteFileViewProviderTest extends BasePsiParsingTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/data";
    }

    public void testDetectXmlFromContentTypeTag() {
        assertTrue(LatteFileViewProvider.detectXmlContentType("{contentType application/xml}\n<root/>"));
    }

    public void testDetectXmlWithTrailingGarbageOnFirstLine() {
        assertTrue(LatteFileViewProvider.detectXmlContentType("{contentType application/xml} {* comment *}\n"));
    }

    public void testDetectXmlAcceptsPrefixedXhtml() {
        assertTrue(LatteFileViewProvider.detectXmlContentType("{contentType application/xhtml+xml}\n"));
    }

    public void testPlainHtmlIsNotDetectedAsXml() {
        assertFalse(LatteFileViewProvider.detectXmlContentType("<html>{$var}</html>"));
    }

    public void testNonXmlContentTypeIsNotDetectedAsXml() {
        assertFalse(LatteFileViewProvider.detectXmlContentType("{contentType text/plain}\nhello"));
    }

    public void testContentTypeMustBeOnFirstLine() {
        assertFalse(LatteFileViewProvider.detectXmlContentType("<html>\n{contentType application/xml}\n</html>"));
    }

    public void testEmptyContentIsNotDetectedAsXml() {
        assertFalse(LatteFileViewProvider.detectXmlContentType(""));
    }

    public void testProviderDefaultsToHtmlWhenNoDocumentCached() {
        // On EDT without a cached Document we intentionally skip VFS I/O and fall back to HTML.
        LightVirtualFile vf = new LightVirtualFile("test.latte", LatteLanguage.INSTANCE, "<html></html>");
        vf.setCharset(CharsetToolkit.UTF8_CHARSET);
        LatteFileViewProvider provider = new LatteFileViewProvider(PsiManager.getInstance(getProject()), vf, false);
        assertSame(HTMLLanguage.INSTANCE, provider.getTemplateDataLanguage());
    }
}
