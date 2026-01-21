import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultaCreditos } from './consulta-creditos';

describe('ConsultaCreditos', () => {
  let component: ConsultaCreditos;
  let fixture: ComponentFixture<ConsultaCreditos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultaCreditos]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsultaCreditos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
