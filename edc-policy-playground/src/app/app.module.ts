/*
 *  Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import {AppHeader} from "./components/header/header.component";

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { PolicyEditorComponent } from './components/policy-editor/policy-editor.component';
import { PolicyService } from './services/policy.service';
import { FormatService } from './services/format.service';
import { PolicyConfigurationStore } from './stores/policy.store';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppHeader,
    PolicyEditorComponent
  ],
  providers: [PolicyService, FormatService, PolicyConfigurationStore],
  bootstrap: [AppComponent]
})
export class AppModule { }
