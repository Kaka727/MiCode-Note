/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.gtask.exception;
/*函数：NetworkFailureException
* 注解人：张冠宇
和上一个函数的基本结构相似，语法方面就不做注释。
网络异常是一个是非运行时异常，他继承了Exception类。
网络异常必须被处理，不然此处的程序无法被执行。
*/
public class NetworkFailureException extends Exception {
    private static final long serialVersionUID = 2107610287180234136L;

    public NetworkFailureException() {
        super();
    }

    public NetworkFailureException(String paramString) {
        super(paramString);
    }

    public NetworkFailureException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
    }
}
