import {Component} from 'angular2/core';
import {DiscussionsStore} from '../state/discussions.store';
import {DiscussionsService} from '../service/discussions.service';
import {RouteConfig} from 'angular2/router';
import {DiscussionListView} from './discussion-list/discussion-list.view';
import {DiscussionDetailView} from './discussion-detail/discussion-detail.view';
import {ROUTER_DIRECTIVES} from 'angular2/router';

@Component({
    template: '<router-outlet></router-outlet>',
    directives: [ROUTER_DIRECTIVES],
    providers: [DiscussionsService, DiscussionsStore]
})
@RouteConfig([
    {path: '', name: 'DiscussionList', component: DiscussionListView, useAsDefault: true},
    {path: '/:id', name: 'DiscussionDetail', component: DiscussionDetailView}
])
export class DiscussionsComponent {

}
