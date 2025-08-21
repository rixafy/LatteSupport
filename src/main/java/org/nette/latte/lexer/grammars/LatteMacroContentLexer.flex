package org.nette.latte.lexer;

import com.intellij.psi.tree.IElementType;
import static org.nette.latte.psi.LatteTypes.*;

%%

%class LatteMacroContentLexer
%extends LatteBaseFlexLexer
%function advance
%type IElementType
%unicode
%ignorecase


WHITE_SPACE=[ \t\r\n]+
SYMBOL = [_[:letter:]][_0-9[:letter:]]*(-[_0-9[:letter:]]+)* //todo: unicode letters
FUNCTION_CALL=[a-zA-Z_][a-zA-Z0-9_]* "("
CLASS_NAME=\\?[a-zA-Z_][a-zA-Z0-9_]*\\[a-zA-Z_][a-zA-Z0-9_\\]* | \\[a-zA-Z_][a-zA-Z0-9_]*
CONTENT_TYPE=[a-zA-Z\-][a-zA-Z0-9\-]*\/[a-zA-Z\-][a-zA-Z0-9\-\.]*
FILE_IMPORT=[\w\-.@()#$%\^&*()!\/]+ ".latte"
SIGNAL=[a-zA-Z\-\:]+ "!"

%%


<YYINITIAL> {

	({CLASS_NAME} | "$" | {FUNCTION_CALL} | "\"" | "'" | "{" | "(" | "[" | "|") .* {
        return T_PHP_CONTENT;
    }

    {CONTENT_TYPE} {
        return T_PHP_CONTENT;
    }

	([0-9]+ | {SYMBOL} | {CLASS_NAME}) {
		return T_PHP_CONTENT;
	}

    {WHITE_SPACE} {
        return T_WHITESPACE;
    }

    {FILE_IMPORT} {
        return T_FILE_PATH;
    }

    {SIGNAL} {
        return T_LINK_DESTINATION;
    }

	[^] {
		return T_MACRO_ARGS;
	}
}