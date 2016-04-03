import {Observable} from 'rxjs/Observable';
declare var jQuery:any;

export abstract class ModalComponent {
    modalId:string;
    title:string;
    loading:boolean = false;

    onSubmit():Observable<any> {
        return new Observable();
    };

    dismiss() {
        console.log('dismiss');
    };

    onError(error:any) {
        console.log(error);
    };

    open() {
        jQuery('#' + this.modalId).modal('show');
    }

    close() {
        jQuery('#' + this.modalId).modal('hide');
    }

    submit():any {
        this.loading = true;
        this.onSubmit().subscribe(
            success=> {
                this.loading = false;
                this.dismiss();
            },
            error=> {
                this.loading = false;
                this.onError(error);
            }
        );
    }
}
