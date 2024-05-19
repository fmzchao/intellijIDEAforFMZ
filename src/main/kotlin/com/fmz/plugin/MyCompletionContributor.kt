package com.fmz.plugin
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiFile
import com.intellij.util.ProcessingContext


object CompletionUtil {
     fun getCompletions(): List<String> {
        return listOf(
            // Add your completion items here
            "exchange.GetName",
            "exchange.GetLabel",
            "exchange.GetUSDCNY",
            "exchange.GetKRWCNY",
            "exchange.GetEURCNY",
            "exchange.GetRate",
            "exchange.GetPeriod",
            "exchange.SetRate",
            "exchange.SetMaxBarLen",
            "exchange.SetTimeout",
            "exchange.GetCurrency",
            "exchange.SetCurrency",
            "exchange.SetBase",
            "exchange.GetQuoteCurrency",
            "exchange.GetTicker",
            "exchange.GetDepth",
            "exchange.GetTrades",
            "exchange.GetRecords",
            "exchange.GetAccount",
            "exchange.Buy",
            "exchange.Sell",
            "exchange.Log",
            "exchange.GetOrders",
            "exchange.GetOrder",
            "exchange.CancelOrder",
            "exchange.GetMinStock",
            "exchange.GetMinPrice",
            "exchange.GetFee",
            "exchange.GetRawJSON",
            "exchange.Go",
            "exchange.IO",
            // Futures
            "exchange.GetPosition",
            "exchange.SetMarginLevel",
            "exchange.SetDirection",
            "exchange.SetContractType",
            "exchange.GetContractType",
            // Added
            "exchange.SetPrecision",
            "exchange.SetProxy",
            // TA
            "TA.MACD",
            "TA.KDJ",
            "TA.RSI",
            "TA.ATR",
            "TA.OBV",
            "TA.MA",
            "TA.EMA",
            "TA.BOLL",
            "TA.Alligator",
            "TA.CMF",
            "TA.Highest",
            "TA.Lowest",
            // other
            "_C", "_G", "_N", "_D", "exchanges", "Log", "LogProfit", "Chart",
            "LogProfitReset", "LogStatus", "EnableLog", "LogReset", "GetCommand", "IsVirtual",
            "Mail", "Sleep", "Dial", "HttpQuery", "Hash", "Unix", "UnixNano", "HMAC", "MD5", "DBExec",
            "GetLastError", "SetErrorFilter", "GetOS", "exchange", "exchanges"
        )
    }
}
class MyCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    resultSet: CompletionResultSet
                ) {
                    val file = parameters.originalFile
                    if (shouldProvideCompletion(file)) {
                        val completions = CompletionUtil.getCompletions()
                        completions.forEach { completion ->
                            resultSet.addElement(LookupElementBuilder.create(completion))
                        }
                    }
                }
            }
        )
    }

    private fun shouldProvideCompletion(file: PsiFile): Boolean {
        val fileName = file.name
        return fileName.endsWith(".js") || fileName.endsWith(".py")
    }

}
