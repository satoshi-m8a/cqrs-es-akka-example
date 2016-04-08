import {Component} from 'angular2/core';
import {Input} from 'angular2/core';
import {ControlGroup} from 'angular2/common';
import {SitesStore} from '../../state/sites.store';
import {Validators} from 'angular2/common';
import {Control} from 'angular2/common';
import {ModalComponent} from '../../../common/component/modal/modal.component';
import {FORM_DIRECTIVES} from 'angular2/common';
@Component({
    selector: 'nv-add-site-modal',
    templateUrl: 'app/sites/component/add-site-modal/add-site-modal.component.html',
    directives: [FORM_DIRECTIVES]
})
export class AddSiteModalComponent extends ModalComponent {
    @Input() modalId:string;
    modalTitle:string = 'サイトを追加';
    form:ControlGroup;
    name:string;

    constructor(private sitesStore:SitesStore) {
        super();

        this.form = new ControlGroup({
            name: new Control(this.name, Validators.required)
        });
    }

    dismiss() {
        this.name = '';
        this.close();
    }

    onSubmit() {
        return this.sitesStore.addSite(this.name);
    }
}
