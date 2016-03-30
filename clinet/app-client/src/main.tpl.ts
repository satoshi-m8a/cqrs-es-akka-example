//<% if (ENV === 'prod') { %>
import {enableProdMode} from 'angular2/core';
//<% } %>
import {bootstrap} from 'angular2/platform/browser';
import {AppComponent} from './app/app.component';
import {ROUTER_PROVIDERS} from 'angular2/router';
import {HTTP_PROVIDERS} from 'angular2/http';
import {FORM_PROVIDERS} from 'angular2/common';
import {Config} from './app/config';
import {provide} from 'angular2/core';

//<% if (ENV === 'prod') { %>
enableProdMode();
//<% } %>

bootstrap(AppComponent, [
    ROUTER_PROVIDERS,
    HTTP_PROVIDERS,
    FORM_PROVIDERS,
    provide(Config, {useValue: new Config()})
]);
