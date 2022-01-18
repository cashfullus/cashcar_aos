package com.cashfulus.cashcarplus.extension

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.button.MaterialButton
import com.cashfulus.cashcarplus.view.UpgradedEdittext

fun View.setStartMargins(leftMarginDp: Float) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.leftMargin = leftMarginDp.toInt().dpToPx(context)
        requestLayout()
    }
}
fun Int.dpToPx(context: Context): Int {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()
}

class BindingAdapter {
    companion object {
        /** 차량 정보의 '국산' 버튼 */
        @JvmStatic
        @BindingAdapter("btnKorea")
        fun setIsKorean(view: MaterialButton, isKorean: Boolean) {
            view.isSelected = isKorean
        }

        @JvmStatic
        @BindingAdapter("btnKoreaAttrChanged")
        fun setIsKoreanInverseBindingListener(view: MaterialButton, listener: InverseBindingListener?) {
            view.setOnClickListener {
                view.isSelected = !view.isSelected
                listener?.onChange()
            }
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "btnKorea", event = "btnKoreaAttrChanged")
        fun getIsKorean(view: MaterialButton): Boolean {
            return view.isSelected
        }


        /** 차량 정보의 '수입' 버튼 */
        // '국산' 버튼만 2-way Binding 적용. '수입'에는 적용시킬 필요 없다.
        @JvmStatic
        @BindingAdapter("btnForeign")
        fun setIsForeign(view: MaterialButton, isKorean: Boolean) {
            view.isSelected = !isKorean
        }
    }
}

@BindingAdapter("visibleOrGone")
fun View.visibleOrGone(isVisible: Boolean?) {
    if (isVisible != null) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}

/*object UpgradedEditTextReverseBindingKt {

}*/

/*@BindingAdapter("app:textAttrChanged")
fun setListener(view: UpgradedEdittext, listener: InverseBindingListener?) {
    if (listener != null) {
        view.getEditText().addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                listener.onChange()
            }
        })
    }
}

@BindingAdapter("app:text")
fun UpgradedEdittext.changeSelectedItem(item: String?) {
    if (item != null && item != getText()) {
        setText(item)
    }
}

@InverseBindingAdapter(attribute = "app:text")
fun UpgradedEdittext.getSelectedItem(): String {
    return getText()
}*/

/*@BindingAdapter("bind:html")
fun html(view: WebView, html: String) {
    view.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
}

@BindingAdapter("bind:loadUrl")
fun bindUrlImage(view: ImageView, url: String) {
    Glide.with(view)
        .load(url)
        .into(view)
}

@BindingAdapter(value = ["selectedValue", "selectedValueAttrChanged"], requireAll = false)
fun bindSpinnerData(pAppCompatSpinner: AppCompatSpinner, newSelectedValue: String?, newTextAttrChanged: InverseBindingListener) {
    pAppCompatSpinner.onItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            newTextAttrChanged.onChange()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
    if (newSelectedValue != null) {
        val pos = (pAppCompatSpinner.adapter as ArrayAdapter<String?>).getPosition(newSelectedValue)
        pAppCompatSpinner.setSelection(pos, true)
    }
}

@InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
fun captureSelectedValue(pAppCompatSpinner: AppCompatSpinner): String? {
    return pAppCompatSpinner.selectedItem as String
}

/*@BindingAdapter("phoneText")
fun phoneText(view: EditText, phone: String) {
    if (phone.length >= 11) {
        val ans = phone.substring(0, 3) + "-" + phone.substring(3, 7) + "-" + phone.substring(7)
        view.setText(ans)
        view.setSelection(view.text.length - (view.text.length - view.selectionStart))
    } else if (phone.length >= 6) {
        val ans = phone.substring(0, 3) + "-" + phone.substring(3, 6) + "-" + phone.substring(6)
        view.setText(ans)
        view.setSelection(view.text.length - (view.text.length - view.selectionStart))
    } else if (phone.length >= 3) {
        val ans = phone.substring(0, 3) + "-" + phone.substring(3)
        view.setText(ans)
        view.setSelection(view.text.length - (view.text.length - view.selectionStart))
    }
}*/