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
/*函数：ActionFailureException
* 注解人：张冠宇
* Exception 中有一个特殊的子类异常RuntimeException叫做运行异常。
     如果在函数内容抛出该异常，函数上可以不用声明。编译一样通过。
     如果在函数上声明了该异常。调用者可以不用进行处理。编译一样通过。
     之所以不用在函数声明，是因为不需要让调用者进行处理。
     当该异常发生时，希望程序停止。因为在运行时，出现了无法继续运算的情况，希望停止程序后，
对代码进行修正。
     对于异常分两种：
1，在编译时被检测的异常。
函数内容抛，函数上抛，调用者要么抛出 要么捕获。
2，编译时不被检测到的异常（运行时异常。RuntimeException以及其子类）
函数内容抛出就可以了 */

public class ActionFailureException extends RuntimeException {
/*注解1：继承的父类RuntimeException
 * 注解人：张冠宇
 * RuntimeException，也就是运行时异常，表示你的代码本身存在BUG
 * RuntimeException，属于应用程序级别的异常，这类异常必须捕  */
    private static final long serialVersionUID = 4425249765923293627L;
    /*注解2：serialVersionUID
     * 注解人：张冠宇
     serialVersionUID相当于java类的身份证。主要用于版本控制。
          作用是序列化时保持版本的兼容性，即在版本升级时反序列化仍保持对象的唯一性。
          在此处的生成方式是根据类名、接口名、成员方法及属性等来生成一个64位的哈希字段  */ 
    public ActionFailureException() {
        super();//当在子类的构造函数需要调用父类的构造函数时，用super（）
    }/*注解3：super
     * 注解人：张冠宇
           当在子类的构造函数需要调用父类的构造函数时，用super（）  */ 

    public ActionFailureException(String paramString) {
        super(paramString);//传递的参数为参数字符串
    }

    public ActionFailureException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);//传递的参数为参数字符串、参数错误
    }
}
