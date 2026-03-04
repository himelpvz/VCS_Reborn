package com.teixeira.vcspace.editor

import org.eclipse.tm4e.languageconfiguration.internal.model.CommentRule

interface CommentRuleProvider {
    val commentRule: CommentRule?
}
