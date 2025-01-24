package domain.composable.dialog.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.composable.basic.button.ActionLeftMainButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.layout.SmallSpacer
import domain.composable.dialog.basic.BeautifulDialog
import domain.composable.dialog.basic.DialogViewModel

@Composable
fun BaseFormDialog(
    formSchema: FormSchema,
    dialogViewModel: DialogViewModel,
) {
    BeautifulDialog(show = formSchema.show, onDismissRequest = {
        formSchema.formDispose()
    }, noPadding = true) {
        BaseCardHeader(
            formSchema.title,
            formSchema.subtitle,
            icon = Icons.Default.Link
        )

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f, fill = false),
        ) {
            formSchema.fields.forEachIndexed { index, field ->
                field.render(
                    dialogViewModel,
                    index = index,
                    isLastOne = index == formSchema.fields.lastIndex
                )
            }
        }
        SmallSpacer()
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            ActionLeftMainButton(text = "保存") {
                formSchema.submitForm()
            }
        }


    }

}