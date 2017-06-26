/*
 * MIT License
 *
 * Copyright (c) 2017 JeeSh
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

jQuery(function ($, undefined) {
    $('#term_demo').terminal('/jeesh/exec', {
        greetings: 'Jeesh REST',
        name: 'jeesh',
        memory: true,
        height: 400,
        width: 950,
        prompt: 'jeesh > ',
        onAjaxError: function (xhr, status, error) {
            switch (xhr.status) {
                case 403:
                    this.echo('Login first');
                    break;
                case 500:
                    this.echo('Something went wrong on the server side... try again later please');
                    break;
                default:
                    this.echo('Unexpected error. Please try again later');
                    console.log("Ajax error boy", status, 'and', error, 'and', xhr);
                    break;
            }
        }
    });
});
