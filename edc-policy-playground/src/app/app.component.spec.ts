import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { AppHeader } from './components/header/header.component';
import { PolicyEditorComponent } from './components/policy-editor/policy-editor.component';
import { PolicyService } from './services/policy.service';
import { FormatService } from './services/format.service';
import { PolicyConfigurationStore } from './stores/policy.store';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('AppComponent', () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      declarations: [AppComponent],
      imports: [BrowserModule, BrowserAnimationsModule, AppHeader, PolicyEditorComponent],
      providers: [PolicyService, FormatService, PolicyConfigurationStore],
    }),
  );

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have as title 'edc-policy-playground'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('edc-policy-playground');
  });

});
