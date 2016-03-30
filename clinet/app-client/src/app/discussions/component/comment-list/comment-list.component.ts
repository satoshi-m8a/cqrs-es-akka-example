import {Component} from 'angular2/core';
import {DiscussionsStore} from '../../state/discussions.store';
@Component({
    selector: 'nv-comment-list',
    templateUrl: 'app/discussions/component/comment-list/comment-list.component.html',
})
export class CommentListComponent {
    constructor(private discussionsStore:DiscussionsStore) {
    }
}

