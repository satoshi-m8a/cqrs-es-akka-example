import {Component} from 'angular2/core';
import {DiscussionsStore} from '../../state/discussions.store';
import {ModalComponent} from '../../../common/component/modal/modal.component';
import {Input} from 'angular2/core';
import {FORM_DIRECTIVES} from 'angular2/common';
import {ControlGroup} from 'angular2/common';
import {Validators} from 'angular2/common';
import {Control} from 'angular2/common';

@Component({
    selector: 'nv-add-comment-modal',
    templateUrl: 'app/discussions/component/add-comment-modal/add-comment-modal.component.html',
    directives: [FORM_DIRECTIVES]
})
export class AddCommentModalComponent extends ModalComponent {
    @Input() modalId:string;
    @Input() discussionId:string;
    modalTitle:string = 'コメントを追加';
    form:ControlGroup;
    text:string;

    constructor(private discussionsStore:DiscussionsStore) {
        super();

        this.form = new ControlGroup({
            text: new Control(this.text, Validators.required)
        });
    }

    dismiss() {
        this.text = '';
        this.close();
    }

    onSubmit() {
        return this.discussionsStore.addComment(this.discussionId, this.text);
    }

}
