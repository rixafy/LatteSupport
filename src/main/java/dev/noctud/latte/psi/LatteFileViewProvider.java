package dev.noctud.latte.psi;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import com.intellij.psi.tree.IElementType;
import dev.noctud.latte.LatteLanguage;
import dev.noctud.latte.utils.LatteHtmlUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class LatteFileViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider implements TemplateLanguageFileViewProvider {

    public static LatteOuterElementType OUTER_LATTE = new LatteOuterElementType("Outer latte");
    private static final Pattern xmlContentType = Pattern.compile("^\\{contentType [^}]*xml[^}]*}.*");
    private static final int SNIFF_LENGTH = 256;
    private static IElementType templateDataElement = new LatteTemplateDataElementType(
        "Outer HTML/XML in Latte",
        LatteLanguage.INSTANCE,
        LatteHtmlUtil.HTML_TOKENS,
        OUTER_LATTE
    );

    private volatile long isXmlCacheStamp = -1;
    private volatile boolean isXmlCacheValue = false;

    public LatteFileViewProvider(PsiManager manager, VirtualFile virtualFile, boolean eventSystemEnabled) {
        super(manager, virtualFile, eventSystemEnabled);
    }

    @NotNull
    @Override
    public Language getBaseLanguage() {
        return LatteLanguage.INSTANCE;
    }

    @NotNull
    public Set<Language> getLanguages() {
        Set<Language> languages = new HashSet<>(3);
        languages.add(LatteLanguage.INSTANCE);
        languages.add(getTemplateDataLanguage());

        return languages;
    }

    @Override
    protected @NotNull MultiplePsiFilesPerDocumentFileViewProvider cloneInner(@NotNull VirtualFile fileCopy) {
        return new LatteFileViewProvider(getManager(), fileCopy, false);
    }

    @NotNull
    @Override
    public Language getTemplateDataLanguage() {
        return isXml() ? XMLLanguage.INSTANCE : HTMLLanguage.INSTANCE;
    }

    @Nullable
    protected PsiFile createFile(@NotNull Language lang) {
        ParserDefinition parser = LanguageParserDefinitions.INSTANCE.forLanguage(lang);
        if (parser == null) {
            return null;
        } else if (lang == XMLLanguage.INSTANCE || lang == HTMLLanguage.INSTANCE) {
            PsiFileImpl file = (PsiFileImpl) parser.createFile(this);
            file.setContentElementType(templateDataElement);
            return file;
        } else {
            return lang == this.getBaseLanguage() ? parser.createFile(this) : null;
        }
    }

    private boolean isXml() {
        VirtualFile vf = getVirtualFile();
        long stamp = vf.getModificationStamp();
        if (stamp == isXmlCacheStamp) {
            return isXmlCacheValue;
        }

        CharSequence head = readHeadSafely(vf);
        if (head == null) {
            return isXmlCacheValue;
        }

        boolean result = detectXmlContentType(head);
        isXmlCacheStamp = stamp;
        isXmlCacheValue = result;
        return result;
    }

    static boolean detectXmlContentType(@NotNull CharSequence head) {
        int newline = indexOfNewline(head);
        CharSequence firstLine = newline > 0 ? head.subSequence(0, newline) : head;
        return xmlContentType.matcher(firstLine).matches();
    }

    private static @Nullable CharSequence readHeadSafely(@NotNull VirtualFile vf) {
        Document doc = FileDocumentManager.getInstance().getCachedDocument(vf);
        if (doc != null) {
            CharSequence seq = doc.getImmutableCharSequence();
            return seq.subSequence(0, Math.min(seq.length(), SNIFF_LENGTH));
        }

        if (ApplicationManager.getApplication().isDispatchThread()) {
            return null;
        }

        try {
            byte[] bytes = vf.contentsToByteArray();
            int end = Math.min(bytes.length, SNIFF_LENGTH);
            return new String(bytes, 0, end, vf.getCharset());
        } catch (Exception e) {
            return null;
        }
    }

    private static int indexOfNewline(@NotNull CharSequence seq) {
        for (int i = 0, n = seq.length(); i < n; i++) {
            char c = seq.charAt(i);
            if (c == '\n' || c == '\r') return i;
        }
        return -1;
    }
}
