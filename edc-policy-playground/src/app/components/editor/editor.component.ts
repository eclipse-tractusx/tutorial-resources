/*******************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/

import { AfterViewInit, Component, Output, ViewChild, ElementRef, ViewEncapsulation, Input } from '@angular/core';
import { EditorView, basicSetup } from 'codemirror';
import { json } from '@codemirror/lang-json';
import { EditorState } from '@codemirror/state';

@Component({
  selector: 'app-editor',
  template: `<div #codemirrorhost class="codemirrorhost"></div>`,
  standalone: true,
  styles: [
    `
      .cm-editor {
        height: 100%;
      }
      .cm-scroller {
        overflow: auto;
      }

      .codemirrorhost {
        height: 100%;
      }
    `,
  ],
  encapsulation: ViewEncapsulation.None,
})
export class EditorComponent implements AfterViewInit {
  @Output() editor!: EditorView;

  @Input()
  get text() {
    return this._text;
  }

  set text(text: string) {
    this._text = text;
    if (this.editor != null) {
      this.editor.dispatch({
        changes: {
          from: 0,
          to: this.editor.state.doc.length,
          insert: text,
        },
      });
    }
  }

  private _text!: string;

  @ViewChild('codemirrorhost')
  codemirrorhost!: ElementRef;

  ngAfterViewInit(): void {
    this.editor = new EditorView({
      doc: this.text,
      extensions: [basicSetup, json(), EditorState.readOnly.of(true)],
      parent: this.codemirrorhost.nativeElement,
    });
  }
}
