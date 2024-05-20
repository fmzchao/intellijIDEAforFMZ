
interface IConsole {
    log(...args: any[]): void;
    error(...args: any[]): void;
}
declare const console: IConsole;
declare const $: any;
declare var BigDecimal: any;
declare var BigFloat: any;
// for consts

declare const PERIOD_M1: number;
declare const PERIOD_M3: number;
declare const PERIOD_M5: number;
declare const PERIOD_M15: number;
declare const PERIOD_M30: number;
declare const PERIOD_H1: number;
declare const PERIOD_H2: number;
declare const PERIOD_H4: number;
declare const PERIOD_H6: number;
declare const PERIOD_H12: number;
declare const PERIOD_D1: number;
declare const PERIOD_D3: number;
declare const PERIOD_W1: number;

/** order is still pending not completed or cancelled */
declare const ORDER_STATE_PENDING: number;
/** order is filled and cancelled */
declare const ORDER_STATE_CLOSED: number;
/** order is cancelled by user or exchange */
declare const ORDER_STATE_CANCELED: number;
/** order is unknown status */
declare const ORDER_STATE_UNKNOWN: number;

/** order type is buy */
declare const ORDER_TYPE_BUY: number;
/** order type is sell */
declare const ORDER_TYPE_SELL: number;

/** order direction is open */
declare const ORDER_OFFSET_OPEN: number;

/** order direction is close */
declare const ORDER_OFFSET_CLOSE: number;

declare const LOG_TYPE_BUY: number;
declare const LOG_TYPE_SELL: number;
declare const LOG_TYPE_CANCEL: number;

/** position direction is long */
declare const PD_LONG: number;
/** position direction is short */
declare const PD_SHORT: number;
/** position direction is long yesterday */
declare const PD_LONG_YD: number;
/** position direction is short yesterday */
declare const PD_SHORT_YD: number;

declare const FUTURES_OP_SET_MARGIN: number;
declare const FUTURES_OP_SET_DIRECTION: number;
declare const FUTURES_OP_SET_CONTRACT_TYPE: number;
declare const FUTURES_OP_GET_POSITION: number;
declare const EXCHANGE_OP_IO_CONTROL: number;

/** basic struct for orders. */
declare interface IOrder {
    /** raw info return from exchange */
    Info: any;
    /** order id may be string or number */
    Id: number | string;
    /** order price */
    Price: number;
    /** order amount */
    Amount: number;
    /** order deal amount */
    DealAmount: number;
    /** order avg price */
    AvgPrice: number;
    /** order status */
    Status: number;
    /** order type should be ORDER_TYPE_BUY or ORDER_TYPE_SELL */
    Type: number;
    /** order offset should be ORDER_OFFSET_OPEN or ORDER_OFFSET_CLOSE */
    Offset?: number;
    /** order contract type */
    ContractType?: string;
}

declare interface IMarketOrder {
    Price: number;
    Amount: number;
}

declare interface IDepth {
    Info: any;
    Time: number;
    Asks: IMarketOrder[];
    Bids: IMarketOrder[];
}

declare interface IPosition {
    Info: any;
    MarginLevel: number;
    Amount: number;
    FrozenAmount: number;
    Price: number;
    Profit: number;
    Type: number;
    ContractType: string;
    Margin: number;
}

declare interface ITrade {
    Id: number | string;
    Time: number;
    Price: number;
    Amount: number;
    Type: number;
}

declare interface ITicker {
    Info: any;
    High: number;
    Low: number;
    Sell: number;
    Buy: number;
    Last: number;
    Volume: number;
    Time: number;
}

declare interface IAccount {
    Info: any;
    Balance: number;
    FrozenBalance: number;
    Stocks: number;
    FrozenStocks: number;
}

declare interface IAsset {
    Currency : string;
    Amount: number;
    FrozenAmount: number;
}

declare interface IRecord {
    Time: number;
    Open: number;
    High: number;
    Low: number;
    Close: number;
    Volume: number;
}

declare interface IMarket {
    Symbol: string;
    BaseAsset: string;
    QuoteAsset: string;
    TickSize?: number;
    AmountSize?: number;
    PricePrecision?: number;
    AmountPrecision?: number;
    MinQty?: number;
    MaxQty?: number;
    MinNotional?: number;
    MaxNotional?: number;
    CtVal?: number;
    Info?: any;
}

interface IGo {
    wait(timeout?: number): any;
}

// exchange
interface IExchange {
    /**
     * Returns a new instance of the Go object
     * @param method The name of the method to call.
     * @param args The arguments to pass to the method.
     */
    Go(method: string, ...args: any[]): IGo;
    /** Gets a position array */
    GetPosition(): IPosition[] | null;
    /** Gets contract current sets */
    GetContractType(): string;
    /**
     * Sets the contract type to use for future exchanges.
     * @param symbol The name of contract type.
     */
    SetContractType(symbol: string): any;
    SetMarginLevel(s: number): void;
    SetDirection(s: string): void;
    CancelOrder(orderId: any, ...extra: any[]): boolean;
    GetOrder(orderId: any): IOrder | null;
    GetOrders(): IOrder[];
    Log(orderType: number, price: number, amount: number, ...args: any[]): void;
    Sell(price: number, amount: number, ...extra: any[]): string | number | null;
    Buy(price: number, amount: number, ...extra: any[]): string | number | null;
    GetRawJSON(): any;
    /** Gets the account struct */
    GetAccount(): IAccount | null;
    GetRecords(period?: number): IRecord[] | null;
    SetMaxBarLen(n: number): void;
    GetTrades(): ITrade[] | null;
    GetTicker(): ITicker | null;
    GetDepth(): IDepth | null;
    GetBaseCurrency(): string;
    GetQuoteCurrency(): string;
    SetProxy(proxy: string): void;
    SetPrecision(pricePrecision: number, amountPrecision: number): void;
    IO(k: string, ...args: any[]): any;
    SetTimeout(n: number): void;
    SetBase(s: string): void;
    GetBase(): string;
    SetCurrency(s: string): void;
    SetRate(n: number): void;
    GetRate(): number;
    GetUSDCNY(): number;
    GetLabel(): string;
    GetCurrency(): string;
    GetPeriod(): number;
    GetName(): string;
    GetMarkets(): { [key: string]: IMarket }|null;
    GetAssets(): IAsset[]|null;
}

// talib
interface Italib {
    WMA(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    WILLR(inPriceHLC: IRecord[], optInTimePeriod?: number): number[];
    WCLPRICE(inPriceHLC: IRecord[]): number[];
    VAR(inReal: number[] | IRecord[], optInTimePeriod?: number, optInNbDev?: number): number[];
    ULTOSC(inPriceHLC: IRecord[], optInTimePeriod1?: number, optInTimePeriod2?: number, optInTimePeriod3?: number): number[];
    TYPPRICE(inPriceHLC: IRecord[]): number[];
    TSF(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    TRIX(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    TRIMA(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    TRANGE(inPriceHLC: IRecord[]): number[];
    TEMA(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    TANH(inReal: number[] | IRecord[]): number[];
    TAN(inReal: number[] | IRecord[]): number[];
    T3(inReal: number[] | IRecord[], optInTimePeriod?: number, optInVFactor?: number): number[];
    SUM(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    STOCHRSI(inReal: number[] | IRecord[], optInTimePeriod?: number, optInFastK_Period?: number, optInFastD_Period?: number, optInFastD_MAType?: number): [number[], number[]];
    STOCHF(inPriceHLC: IRecord[], optInFastK_Period?: number, optInFastD_Period?: number, optInFastD_MAType?: number): [number[], number[]];
    STOCH(inPriceHLC: IRecord[], optInFastK_Period?: number, optInSlowK_Period?: number, optInSlowK_MAType?: number, optInSlowD_Period?: number, optInSlowD_MAType?: number): [number[], number[]];
    STDDEV(inReal: number[] | IRecord[], optInTimePeriod?: number, optInNbDev?: number): number[];
    SQRT(inReal: number[] | IRecord[]): number[];
    SMA(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    SINH(inReal: number[] | IRecord[]): number[];
    SIN(inReal: number[] | IRecord[]): number[];
    SAREXT(inPriceHL: IRecord[], optInStartValue?: number, optInOffsetOnReverse?: number, optInAccelerationInitLong?: number, optInAccelerationLong?: number, optInAccelerationMaxLong?: number, optInAccelerationInitShort?: number, optInAccelerationShort?: number, optInAccelerationMaxShort?: number): number[];
    SAR(inPriceHL: IRecord[], optInAcceleration?: number, optInMaximum?: number): number[];
    RSI(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    ROCR100(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    ROCR(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    ROCP(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    ROC(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    PPO(inReal: number[] | IRecord[], optInFastPeriod?: number, optInSlowPeriod?: number, optInMAType?: number): number[];
    PLUS_DM(inPriceHL: IRecord[], optInTimePeriod?: number): number[];
    PLUS_DI(inPriceHLC: IRecord[], optInTimePeriod?: number): number[];
    OBV(inReal: number[] | IRecord[], inPriceV: IRecord[]): number[];
    NATR(inPriceHLC: IRecord[], optInTimePeriod?: number): number[];
    MOM(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    MINUS_DM(inPriceHL: IRecord[], optInTimePeriod?: number): number[];
    MINUS_DI(inPriceHLC: IRecord[], optInTimePeriod?: number): number[];
    MINMAXINDEX(inReal: number[] | IRecord[], optInTimePeriod?: number): [number[], number[]];
    MINMAX(inReal: number[] | IRecord[], optInTimePeriod?: number): [number[], number[]];
    MININDEX(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    MIN(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    MIDPRICE(inPriceHL: IRecord[], optInTimePeriod?: number): number[];
    MIDPOINT(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    MFI(inPriceHLCV: IRecord[], optInTimePeriod?: number): number[];
    MEDPRICE(inPriceHL: IRecord[]): number[];
    MAXINDEX(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    MAX(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    MAMA(inReal: number[] | IRecord[], optInFastLimit?: number, optInSlowLimit?: number): [number[], number[]];
    MACDFIX(inReal: number[] | IRecord[], optInSignalPeriod?: number): [number[], number[], number[]];
    MACDEXT(inReal: number[] | IRecord[], optInFastPeriod?: number, optInFastMAType?: number, optInSlowPeriod?: number, optInSlowMAType?: number, optInSignalPeriod?: number, optInSignalMAType?: number): [number[], number[], number[]];
    MACD(inReal: number[] | IRecord[], optInFastPeriod?: number, optInSlowPeriod?: number, optInSignalPeriod?: number): [number[], number[], number[]];
    MA(inReal: number[] | IRecord[], optInTimePeriod?: number, optInMAType?: number): number[];
    LOG10(inReal: number[] | IRecord[]): number[];
    LN(inReal: number[] | IRecord[]): number[];
    LINEARREG_SLOPE(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    LINEARREG_INTERCEPT(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    LINEARREG_ANGLE(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    LINEARREG(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    KAMA(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    HT_TRENDMODE(inReal: number[] | IRecord[]): number[];
    HT_TRENDLINE(inReal: number[] | IRecord[]): number[];
    HT_SINE(inReal: number[] | IRecord[]): [number[], number[]];
    HT_PHASOR(inReal: number[] | IRecord[]): [number[], number[]];
    HT_DCPHASE(inReal: number[] | IRecord[]): number[];
    HT_DCPERIOD(inReal: number[] | IRecord[]): number[];
    FLOOR(inReal: number[] | IRecord[]): number[];
    EXP(inReal: number[] | IRecord[]): number[];
    EMA(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    DX(inPriceHLC: IRecord[], optInTimePeriod?: number): number[];
    DEMA(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    COSH(inReal: number[] | IRecord[]): number[];
    COS(inReal: number[] | IRecord[]): number[];
    CMO(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    CEIL(inReal: number[] | IRecord[]): number[];
    CDLXSIDEGAP3METHODS(inPriceOHLC: IRecord[]): number[];
    CDLUPSIDEGAP2CROWS(inPriceOHLC: IRecord[]): number[];
    CDLUNIQUE3RIVER(inPriceOHLC: IRecord[]): number[];
    CDLTRISTAR(inPriceOHLC: IRecord[]): number[];
    CDLTHRUSTING(inPriceOHLC: IRecord[]): number[];
    CDLTASUKIGAP(inPriceOHLC: IRecord[]): number[];
    CDLTAKURI(inPriceOHLC: IRecord[]): number[];
    CDLSTICKSANDWICH(inPriceOHLC: IRecord[]): number[];
    CDLSTALLEDPATTERN(inPriceOHLC: IRecord[]): number[];
    CDLSPINNINGTOP(inPriceOHLC: IRecord[]): number[];
    CDLSHORTLINE(inPriceOHLC: IRecord[]): number[];
    CDLSHOOTINGSTAR(inPriceOHLC: IRecord[]): number[];
    CDLSEPARATINGLINES(inPriceOHLC: IRecord[]): number[];
    CDLRISEFALL3METHODS(inPriceOHLC: IRecord[]): number[];
    CDLRICKSHAWMAN(inPriceOHLC: IRecord[]): number[];
    CDLPIERCING(inPriceOHLC: IRecord[]): number[];
    CDLONNECK(inPriceOHLC: IRecord[]): number[];
    CDLMORNINGSTAR(inPriceOHLC: IRecord[], optInPenetration?: number): number[];
    CDLMORNINGDOJISTAR(inPriceOHLC: IRecord[], optInPenetration?: number): number[];
    CDLMATHOLD(inPriceOHLC: IRecord[], optInPenetration?: number): number[];
    CDLMATCHINGLOW(inPriceOHLC: IRecord[]): number[];
    CDLMARUBOZU(inPriceOHLC: IRecord[]): number[];
    CDLLONGLINE(inPriceOHLC: IRecord[]): number[];
    CDLLONGLEGGEDDOJI(inPriceOHLC: IRecord[]): number[];
    CDLLADDERBOTTOM(inPriceOHLC: IRecord[]): number[];
    CDLKICKINGBYLENGTH(inPriceOHLC: IRecord[]): number[];
    CDLKICKING(inPriceOHLC: IRecord[]): number[];
    CDLINVERTEDHAMMER(inPriceOHLC: IRecord[]): number[];
    CDLINNECK(inPriceOHLC: IRecord[]): number[];
    CDLIDENTICAL3CROWS(inPriceOHLC: IRecord[]): number[];
    CDLHOMINGPIGEON(inPriceOHLC: IRecord[]): number[];
    CDLHIKKAKEMOD(inPriceOHLC: IRecord[]): number[];
    CDLHIKKAKE(inPriceOHLC: IRecord[]): number[];
    CDLHIGHWAVE(inPriceOHLC: IRecord[]): number[];
    CDLHARAMICROSS(inPriceOHLC: IRecord[]): number[];
    CDLHARAMI(inPriceOHLC: IRecord[]): number[];
    CDLHANGINGMAN(inPriceOHLC: IRecord[]): number[];
    CDLHAMMER(inPriceOHLC: IRecord[]): number[];
    CDLGRAVESTONEDOJI(inPriceOHLC: IRecord[]): number[];
    CDLGAPSIDESIDEWHITE(inPriceOHLC: IRecord[]): number[];
    CDLEVENINGSTAR(inPriceOHLC: IRecord[], optInPenetration?: number): number[];
    CDLEVENINGDOJISTAR(inPriceOHLC: IRecord[], optInPenetration?: number): number[];
    CDLENGULFING(inPriceOHLC: IRecord[]): number[];
    CDLDRAGONFLYDOJI(inPriceOHLC: IRecord[]): number[];
    CDLDOJISTAR(inPriceOHLC: IRecord[]): number[];
    CDLDOJI(inPriceOHLC: IRecord[]): number[];
    CDLDARKCLOUDCOVER(inPriceOHLC: IRecord[], optInPenetration?: number): number[];
    CDLCOUNTERATTACK(inPriceOHLC: IRecord[]): number[];
    CDLCONCEALBABYSWALL(inPriceOHLC: IRecord[]): number[];
    CDLCLOSINGMARUBOZU(inPriceOHLC: IRecord[]): number[];
    CDLBREAKAWAY(inPriceOHLC: IRecord[]): number[];
    CDLBELTHOLD(inPriceOHLC: IRecord[]): number[];
    CDLADVANCEBLOCK(inPriceOHLC: IRecord[]): number[];
    CDLABANDONEDBABY(inPriceOHLC: IRecord[], optInPenetration?: number): number[];
    CDL3WHITESOLDIERS(inPriceOHLC: IRecord[]): number[];
    CDL3STARSINSOUTH(inPriceOHLC: IRecord[]): number[];
    CDL3OUTSIDE(inPriceOHLC: IRecord[]): number[];
    CDL3LINESTRIKE(inPriceOHLC: IRecord[]): number[];
    CDL3INSIDE(inPriceOHLC: IRecord[]): number[];
    CDL3BLACKCROWS(inPriceOHLC: IRecord[]): number[];
    CDL2CROWS(inPriceOHLC: IRecord[]): number[];
    CCI(inPriceHLC: IRecord[], optInTimePeriod?: number): number[];
    BOP(inPriceOHLC: IRecord[]): number[];
    BBANDS(inReal: number[] | IRecord[], optInTimePeriod?: number, optInNbDevUp?: number, optInNbDevDn?: number, optInMAType?: number): [number[], number[], number[]];
    AVGPRICE(inPriceOHLC: IRecord[]): number[];
    ATR(inPriceHLC: IRecord[], optInTimePeriod?: number): number[];
    ATAN(inReal: number[] | IRecord[]): number[];
    ASIN(inReal: number[] | IRecord[]): number[];
    AROONOSC(inPriceHL: IRecord[], optInTimePeriod?: number): number[];
    AROON(inPriceHL: IRecord[], optInTimePeriod?: number): [number[], number[]];
    APO(inReal: number[] | IRecord[], optInFastPeriod?: number, optInSlowPeriod?: number, optInMAType?: number): number[];
    ADXR(inPriceHLC: IRecord[], optInTimePeriod?: number): number[];
    ADX(inPriceHLC: IRecord[], optInTimePeriod?: number): number[];
    ADOSC(inPriceHLCV: IRecord[], optInFastPeriod?: number, optInSlowPeriod?: number): number[];
    AD(inPriceHLCV: IRecord[]): number[];
    ACOS(inReal: number[] | IRecord[]): number[];
}

// TA
interface ITA {
    CMF(inReal: number[] | IRecord[], optInTimePeriod?: IRecord[]): number[];
    Alligator(inReal: number[] | IRecord[], jawLength?: number, teethLength?: number, lipsLength?: number): [number[], number[], number[]];
    ATR(inPriceHLC: IRecord[], optInTimePeriod?: number): number[];
    OBV(inReal: number[] | IRecord[], inPriceV: IRecord[]): number[];
    RSI(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    KDJ(inReal: number[] | IRecord[], period?: number, kPeriod?: number, dPeriod?: number): [number[], number[], number[]];
    BOLL(inReal: number[] | IRecord[], period?: number, multiplier?: number): [number[], number[], number[]];
    MACD(inReal: number[] | IRecord[], optInFastPeriod?: number, optInSlowPeriod?: number, optInSignalPeriod?: number): [number[], number[], number[]];
    EMA(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    SMA(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    MA(inReal: number[] | IRecord[], optInTimePeriod?: number): number[];
    Highest(inReal: number[] | IRecord[], period?: number, attr?: string): number;
    Lowest(inReal: number[] | IRecord[], period?: number, attr?: string): number;
}

interface IDBExecRet {
    values: Array<Array<any>>;
    columns: string[];
}

interface IDial {
    read(timeout?: number): string | ArrayBuffer | null | undefined;
    write(data: string | ArrayBuffer): void;
    exec(sql: string, ...extra: any[]): IDBExecRet | null;
    fd(): number;
    close(): void;
}

interface IWASMInstance {
    callFunction(name: string, ...args: any[]): any;
}

interface IWASM {
    parseModule(data: string | ArrayBuffer): any;
    buildInstance(m: any, options?: any): IWASMInstance | null;
}


interface IEventMsg {
    Seq: number;
    Event: string;
    ThreadId: number;
    Index: number;
    Nano: number;
    Symbol: string;
    Ticker?: ITicker;
}

interface IChart {
    add(series: number, data: any[], index?: number): void;
    reset(remain?: number): void;
    del(series: number): void;
    update(options: object): void;
}

declare interface IHttpOptions {
    method?: string;
    body?: string | ArrayBuffer;
    charset?: string;
    cookie?: string;
    profile?: string;
    debug?: boolean;
    format?: "base64" | "hex";
    close?: boolean;
    headers?: { [key: string]: any };
    timeout?: number;
}

declare interface IHttpRet {
    StatusCode: number;
    Trace?: any;
    Header: { [key: string]: string[] };
    Body: string | ArrayBuffer | null;
}

// globals

declare const talib: Italib;
declare const TA: ITA;
declare const wasm: IWASM;


declare const exchange: IExchange;
declare const exchanges: IExchange[];


declare function Version(): string;
declare function Sleep(millisecond: number): void;
declare function IsVirtual(): boolean;
declare function Mail(smtpServer: string, smtpUsername: string, smtpPassword: string, mailTo: string, title: string, body: string): boolean;
declare function SetErrorFilter(filters: string): void;
declare function GetPid(): number;
declare function GetLastError(): string | null;
declare function GetCommand(): string | null;
declare function GetMeta(): string | null;
declare function Dial(address: string|number, timeout?: number): IDial | null;

declare function HttpQuery(url: string, postData?: string | ArrayBuffer, cookies?: string, headers?: string | { [key: string]: string }): string | null;
declare function HttpQuery(url: string, options: { debug: true } & IHttpOptions): IHttpRet | null;
declare function HttpQuery(url: string, options?: { debug?: false } & IHttpOptions): string | null;


declare function Encode(algo: string, inputFormat: "hex" | "base64" | "raw" | "string", outputFormat: "hex" | "base64" | "raw" | "string", data: string | ArrayBuffer, keyFormat?: string, key?: string): string | ArrayBuffer;
declare function UnixNano(): number;
declare function Unix(): number;
declare function GetOS(): string;
declare function SysInfo(attr: string): any;
declare function MD5(data: string): string;
declare function DBExec(sql: string, ...extra: any[]): IDBExecRet | null;
declare function UUID(): string;
declare function EventLoop(timeout?: number): IEventMsg | null;

declare function _G(k: string, v?: any): any;
declare function _D(timestamp?: number | Date, fmt?: string): string;
declare function _N(num: number, precision: number): number;
declare function _C(pfn: Function, ...args: any[]): any;
declare function _Cross(arr1: number[], arr2: number[]): boolean;
declare function JSONParse(s: string): object;

declare function Log(s: any, ...extra: any[]): void;
declare function LogProfit(profit: number, ...extra: any[]): void;
declare function LogProfitReset(remain?: number): void;
declare function LogStatus(s: any, ...extra: any[]): void;
declare function EnableLog(enable: boolean): void;
declare function Chart(options: object | Array<object>): IChart;
declare function KLineChart(options: object | Array<object>): IChart;
declare function LogReset(remain?: number): void;
declare function LogVacuum(): void;


declare interface IThreadRet {
    id: number;
    terminated: boolean;
    elapsed: number;
    ret: any;
}

declare function __Thread(f: Function, ...args: any[]): number;
declare function __Thread(item: Array<any>, ...items: Array<any>[]): number;
declare function __threadPeekMessage(timeout?: number): any | null;
declare function __threadPostMessage(threadId: number, msg: any): void;
declare function __threadJoin(threadId: number, timeout?: number): IThreadRet | null;
declare function __threadTerminate(threadId: number): void;
declare function __threadGetData(threadId: number, key: string): any;
declare function __threadSetData(threadId: number, key: string, value: any): void;
declare function __threadId(): number;
declare function __threadPending(running?:boolean): number;


