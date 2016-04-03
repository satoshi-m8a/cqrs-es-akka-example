import {Component} from 'angular2/core';
import {ROUTER_DIRECTIVES} from 'angular2/router';
import {DiscussionsStore} from '../../state/discussions.store';
@Component({
    selector: 'nv-discussion-list',
    templateUrl: 'app/discussions/component/discussion-list/discussion-list.component.html',
    directives: [ROUTER_DIRECTIVES]
})
export class DiscussionListComponent {
    constructor(private discussionsStore:DiscussionsStore) {
        discussionsStore.getDiscussions();
    }

}
