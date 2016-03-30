import {Component} from 'angular2/core';
import {DiscussionListComponent} from '../../component/discussion-list/discussion-list.component';
@Component({
    templateUrl: 'app/discussions/view/discussion-list/discussion-list.view.html',
    directives: [DiscussionListComponent]
})
export class DiscussionListView {
    constructor() {
        console.log('discussion list');
    }
}
